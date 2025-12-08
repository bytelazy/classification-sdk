package com.example.sdk;

import com.example.sdk.model.Rule;

import java.util.List;

/**
 * Represents the outcome of a classification operation for a single piece of
 * data. Contains the input data and a list of matched rules. Provides a
 * convenience method to check if any rule was matched.
 */
public class DetectionResult {
    private final String data;
    private final List<Rule> matchedRules;

    public DetectionResult(String data, List<Rule> matchedRules) {
        this.data = data;
        this.matchedRules = matchedRules;
    }

    public String getData() { return data; }
    public List<Rule> getMatchedRules() { return matchedRules; }

    /**
     * Return true if one or more rules matched the input.
     */
    public boolean hasMatch() {
        return matchedRules != null && !matchedRules.isEmpty();
    }
}