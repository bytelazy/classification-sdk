package com.example.sdk.config;

/**
 * Holds global SDK configuration values which can be overridden via system
 * properties or environment variables. Defaults are provided for all values
 * to enable out-of-the-box usage for local testing.
 */
public class SdkConfig {

    // Default policy endpoint for the mock service
    private static final String DEFAULT_POLICY_URL =
        "http://localhost:8081/api/v1/rules";

    // Default polling interval is 15 minutes
    private static final int DEFAULT_POLL_INTERVAL_SECONDS = 900;

    // Stored last ETag for conditional requests
    private static volatile String lastETag;

    /**
     * Determine the URL of the policy configuration service. Checked in order:
     * JVM system property 'sdk.policy.url', environment variable 'SDK_POLICY_URL',
     * then falls back to DEFAULT_POLICY_URL.
     */
    public static String getPolicyUrl() {
        String url = System.getProperty("sdk.policy.url");
        if (url == null || url.isEmpty()) {
            url = System.getenv("SDK_POLICY_URL");
        }
        return (url == null || url.isEmpty()) ? DEFAULT_POLICY_URL : url;
    }

    /**
     * Determine the polling interval. Checked in order:
     * JVM system property 'sdk.rule.poll.interval.seconds', environment variable
     * 'SDK_RULE_POLL_INTERVAL_SECONDS', then falls back to default.
     */
    public static int getPollIntervalSeconds() {
        String val = System.getProperty("sdk.rule.poll.interval.seconds");
        if (val == null || val.isEmpty()) {
            val = System.getenv("SDK_RULE_POLL_INTERVAL_SECONDS");
        }
        if (val != null && !val.isEmpty()) {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException ignore) {
                // ignore and fall back to default
            }
        }
        return DEFAULT_POLL_INTERVAL_SECONDS;
    }

    public static String getLastETag() {
        return lastETag;
    }

    public static void setLastETag(String eTag) {
        lastETag = eTag;
    }
}
