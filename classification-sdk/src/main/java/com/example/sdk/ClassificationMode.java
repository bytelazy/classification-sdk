package com.example.sdk;

/**
 * Defines how the classifier should behave when multiple rules match a given
 * input. In TOP_MATCH_ONLY mode only the highest priority match is returned.
 * In ALL_MATCHES mode all matching rules are returned for auditing or
 * analysis purposes.
 */
public enum ClassificationMode {
    TOP_MATCH_ONLY,
    ALL_MATCHES
}
