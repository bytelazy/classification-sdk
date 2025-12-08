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

// import dictionary matcher for dictionary-based rule support
import com.example.sdk.DictionaryMatcher;

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
            if (!rule.isEnabled()) {
                continue;
            }
            // Evaluate the rule against the input value. When operating in
            // TOP_MATCH_ONLY mode the loop terminates after the first
            // successful match due to the rules being ordered by priority.
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

    /**
     * Determine whether the input matches any matcher definitions for a given rule.
     *
     * <p>The matcher type dictates how the pattern is evaluated:
     * <ul>
     *   <li><strong>regex</strong> – the pattern is compiled into a
     *       {@link java.util.regex.Pattern} and a search is performed on the input.</li>
     *   <li><strong>fuzzy</strong> – the pattern is compared against the input
     *       using a Levenshtein distance algorithm via {@link FuzzyMatcher}.</li>
     *   <li><strong>dictionary</strong> – the pattern is treated as a comma or
     *       semicolon separated list of keywords. If any keyword appears in the
     *       input the matcher succeeds. See {@link DictionaryMatcher} for
     *       details.</li>
     * </ul>
     * Additional matcher types can be added without modifying this method by
     * implementing custom logic or by leveraging a pluggable matcher registry.
     * </p>
     *
     * @param rule the classification rule
     * @param data the input data to test
     * @return {@code true} if the data satisfies any matcher of the rule,
     *         {@code false} otherwise
     */
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
                // Compare the pattern and data using a fuzzy match. Note that
                // FuzzyMatcher expects (pattern, text) order for its arguments.
                if (FuzzyMatcher.matches(m.getPattern(), data)) {
                    return true;
                }
            } else if ("dictionary".equalsIgnoreCase(type)) {
                // Dictionary matching checks whether any of the comma-separated
                // keywords defined by the pattern appear in the input. Matching
                // is case-insensitive.
                if (DictionaryMatcher.matches(data, m.getPattern())) {
                    return true;
                }
            }
            // ignore unknown matcher types
        }
        return false;
    }
}