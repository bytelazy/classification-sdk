package com.example.sdk.model;

/**
 * Definition of a matching component within a rule. A matcher can be of different
 * types (e.g. regex, dictionary, semantic_model) and contains the pattern needed
 * for detection along with an optional confidence. Confidence is unused for regex
 * matchers but reserved for future semantic matchers.
 */
public class MatcherDef {
    private String type;        // regex/dictionary/semantic_model...
    private String pattern;     // regex or other pattern expression
    private Double confidence;  // optional confidence threshold

    public MatcherDef() {}

    public MatcherDef(String type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
}
