package com.example.sdk;

import com.example.sdk.model.MatcherDef;
import com.example.sdk.model.Rule;
import com.example.sdk.model.Ruleset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

// import dictionary matcher for dictionary-based rule support
import com.example.sdk.DictionaryMatcher;

/**
 * Core classification engine that executes the rule matchers against input
 * strings. This version removes logging dependencies to keep the SDK fully
 * self-contained for offline execution.
 */
public class DetectionEngine {

    // Simple LRU cache for compiled regex patterns to avoid re-compiling
    private static final Map<String, Pattern> PATTERN_CACHE =
            Collections.synchronizedMap(new LinkedHashMap<String, Pattern>(100, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                    return size() > 100;
                }
            });

    public DetectionResult detect(String data, ClassificationMode mode) {
        Ruleset ruleset = RuleManager.getCurrentRuleset();
        if (ruleset == null || ruleset.getRules() == null) {
            return new DetectionResult(data, Collections.emptyList());
        }

        List<Rule> matched = new ArrayList<>();
        List<Rule> sorted = new ArrayList<>(ruleset.getRules());
        sorted.sort(Comparator.comparing(Rule::getPriority).reversed());

        for (Rule rule : sorted) {
            if (!rule.isEnabled()) {
                continue;
            }
            if (matches(rule, data)) {
                matched.add(rule);
                if (mode == ClassificationMode.TOP_MATCH_ONLY) {
                    break;
                }
            }
        }

        return new DetectionResult(data, matched);
    }

    private boolean matches(Rule rule, String data) {
        for (MatcherDef m : rule.getMatchers()) {
            String type = m.getType();
            if (type == null) {
                continue;
            }
            if ("regex".equalsIgnoreCase(type)) {
                Pattern p = PATTERN_CACHE.computeIfAbsent(m.getPattern(), Pattern::compile);
                if (p.matcher(data).find()) {
                    return true;
                }
            } else if ("fuzzy".equalsIgnoreCase(type)) {
                if (FuzzyMatcher.matches(m.getPattern(), data)) {
                    return true;
                }
            } else if ("dictionary".equalsIgnoreCase(type)) {
                if (DictionaryMatcher.matches(data, m.getPattern())) {
                    return true;
                }
            }
        }
        return false;
    }
}
