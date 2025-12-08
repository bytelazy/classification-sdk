package com.example.sdk;

import com.example.sdk.model.MatcherDef;
import com.example.sdk.model.Rule;
import com.example.sdk.model.Ruleset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Core classification engine that executes the rule matchers against input
 * strings. Caches compiled regular expressions and respects rule priority
 * ordering. The detection mode determines whether only the top match or all
 * matches are returned.
 */
public class DetectionEngine {

    private static final Logger log = LoggerFactory.getLogger(DetectionEngine.class);

    // Simple LRU cache for compiled regex patterns to avoid re-compiling
    private static final Map<String, Pattern> PATTERN_CACHE =
            Collections.synchronizedMap(new LinkedHashMap<String, Pattern>(100, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                    return size() > 100;
                }
            });

    /**
     * Detect sensitive data within the provided string according to the
     * currently active ruleset and return a DetectionResult.
     *
     * @param data the value to inspect
     * @param mode determines whether to stop at the first match or return all
     * @return the detection result containing matched rules
     */
    public DetectionResult detect(String data, ClassificationMode mode) {
        Ruleset ruleset = RuleManager.getCurrentRuleset();
        if (ruleset == null || ruleset.getRules() == null) {
            return new DetectionResult(data, Collections.emptyList());
        }

        List<Rule> matched = new ArrayList<>();
        // Sort rules by descending priority so that the first match is the highest priority
        List<Rule> sorted = new ArrayList<>(ruleset.getRules());
        sorted.sort(Comparator.comparing(Rule::getPriority).reversed());

        for (Rule rule : sorted) {
            if (!rule.isEnabled()) continue;
            if (matches(rule, data)) {
                matched.add(rule);
                if (mode == ClassificationMode.TOP_MATCH_ONLY) {
                    break;
                }
            }
        }

        if (!matched.isEmpty()) {
            log.info("Matched {} rule(s) for data: {}", matched.size(), data);
        }

        return new DetectionResult(data, matched);
    }

    // Determine whether the input matches any matcher definitions for this rule
    private boolean matches(Rule rule, String data) {
        for (MatcherDef m : rule.getMatchers()) {
            String type = m.getType();
            if ("regex".equalsIgnoreCase(type)) {
                Pattern p = PATTERN_CACHE.computeIfAbsent(m.getPattern(), Pattern::compile);
                if (p.matcher(data).find()) {
                    return true;
                }
            } else if ("fuzzy".equalsIgnoreCase(type)) {
                // Fuzzy matching compares the entire pattern string with the input data.
                // In a real implementation you might extract tokens or adjust the
                // threshold dynamically.  Here we simply check if the pattern
                // approximately matches the input using Levenshtein distance.
                if (FuzzyMatcher.matches(m.getPattern(), data)) {
                    return true;
                }
            }
        }
        return false;
    }
}