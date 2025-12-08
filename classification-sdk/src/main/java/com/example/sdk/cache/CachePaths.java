package com.example.sdk.cache;

/**
 * Calculates filesystem paths for rule caches. The cache directory can be
 * specified using a JVM system property (sdk.rule.cache.dir) or the
 * environment variable SDK_RULE_CACHE_DIR. If neither is set it falls back
 * to a hidden directory under the user's home directory.
 */
public class CachePaths {

    public static String getCacheDir() {
        String dir = System.getProperty("sdk.rule.cache.dir");
        if (dir == null || dir.isEmpty()) {
            dir = System.getenv("SDK_RULE_CACHE_DIR");
        }
        if (dir == null || dir.isEmpty()) {
            dir = System.getProperty("user.home") + "/.classification-sdk/";
        }
        return dir.endsWith("/") ? dir : dir + "/";
    }

    /**
     * File storing the latest ruleset regardless of version. Always overwritten
     * on update so that the SDK can quickly load the last known good config.
     */
    public static String getLatestCacheFile() {
        return getCacheDir() + "rules-cache.json";
    }

    /**
     * File storing a versioned copy of the ruleset for debugging and rollback
     * purposes. The version is embedded in the filename.
     */
    public static String getVersionCacheFile(String version) {
        return getCacheDir() + "rules-cache-" + version + ".json";
    }
}
