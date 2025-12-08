package com.example.sdk;

import java.util.Arrays;
import java.util.List;

/**
 * Simple dictionary matcher used by the {@link com.example.sdk.DetectionEngine}.
 * <p>
 * A dictionary matcher compares the input data against a list of predefined
 * words or phrases. The list of dictionary entries is defined on a per-rule
 * basis via the {@link com.example.sdk.model.MatcherDef} pattern property.
 * Multiple entries should be separated by commas. Matching is case‐insensitive
 * and ignores leading/trailing whitespace. If any dictionary entry is found
 * within the input data, the matcher is considered a hit.
 * </p>
 * <p>
 * Note that this implementation is intentionally simple and only suitable
 * for demonstration or prototyping purposes. In a production environment you
 * might load large dictionaries from external files, perform normalization
 * (e.g. Unicode folding) or use more sophisticated tokenization and matching
 * strategies. This class provides a clear extension point for such
 * enhancements.
 * </p>
 */
public final class DictionaryMatcher {

    private DictionaryMatcher() {
        // util class
    }

    /**
     * Determines whether the supplied text contains any of the dictionary
     * entries defined by the pattern string. The pattern string must contain
     * one or more dictionary entries separated by commas. Leading and
     * trailing whitespace around each entry will be trimmed. Matching is
     * performed using {@link String#toLowerCase()} for case‐insensitive
     * comparison.
     *
     * @param data    the input text to test, must not be {@code null}
     * @param pattern the dictionary pattern string, must not be {@code null}
     * @return {@code true} if any dictionary entry is contained in the text,
     * {@code false} otherwise
     */
    public static boolean matches(String data, String pattern) {
        if (data == null || pattern == null) {
            return false;
        }
        // split the pattern on commas and trim whitespace
        List<String> entries = Arrays.asList(pattern.split("[,;]"));
        String lower = data.toLowerCase();
        for (String entry : entries) {
            String trimmed = entry.trim().toLowerCase();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (lower.contains(trimmed)) {
                return true;
            }
        }
        return false;
    }
}