package com.example.sdk;

/**
 * Utility class implementing a simple fuzzy matching algorithm
 * based on Levenshtein distance.  This matcher is designed to
 * demonstrate how you could plug in a more advanced rule engine
 * without changing the public SDK API.  The algorithm computes the
 * edit distance between the pattern and the input and returns true
 * when the distance is below a configurable threshold.  In this
 * example the threshold is fixed at 2 edits.
 *
 * <p>Note: For production use consider integrating a mature
 * approximate string matching library or machine learning model.
 * </p>
 */
public final class FuzzyMatcher {
    private FuzzyMatcher() {}

    /**
     * Determine if the input text approximately matches the pattern.  If
     * the edit distance between the two strings is less than or equal
     * to {@code MAX_DISTANCE} this method returns true.
     *
     * @param pattern pattern to compare against
     * @param text    input text
     * @return true if similar, false otherwise
     */
    public static boolean matches(String pattern, String text) {
        final int MAX_DISTANCE = 2;
        int distance = levenshteinDistance(pattern, text);
        return distance <= MAX_DISTANCE;
    }

    /**
     * Compute the Levenshtein edit distance between two strings.  This
     * implementation is intentionally simple for demonstration
     * purposes.  It runs in O(n*m) time and O(n*m) space where n and
     * m are the lengths of the input strings.
     *
     * @param s first string
     * @param t second string
     * @return edit distance
     */
    private static int levenshteinDistance(String s, String t) {
        int[][] dp = new int[s.length() + 1][t.length() + 1];
        for (int i = 0; i <= s.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= t.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= t.length(); j++) {
                int cost = s.charAt(i - 1) == t.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s.length()][t.length()];
    }
}