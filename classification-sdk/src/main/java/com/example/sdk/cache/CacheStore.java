package com.example.sdk.cache;

import com.example.sdk.model.Ruleset;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Persists and loads rule configurations from the filesystem. Maintains both
 * the most recent ruleset and backups by version to aid in debugging and
 * rollback scenarios. Caches use JSON serialization via Jackson.
 */
public class CacheStore {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Load the latest cached ruleset from disk. If no cache exists or parsing
     * fails this method returns null and the caller should fall back to
     * bootstrap rules or remote fetch.
     */
    public static Ruleset loadLatest() {
        File file = new File(CachePaths.getLatestCacheFile());
        if (!file.exists()) {
            return null;
        }
        try {
            return MAPPER.readValue(file, Ruleset.class);
        } catch (Exception e) {
            return null; // treat as no cache
        }
    }

    /**
     * Save the provided ruleset to disk. This method writes both the latest
     * cache file and a versioned backup. IO errors are swallowed to avoid
     * breaking the caller.
     */
    public static void save(Ruleset ruleset) {
        try {
            ensureDir();

            // Persist versioned backup
            String version = ruleset.getVersion();
            File versionFile = new File(CachePaths.getVersionCacheFile(version));
            MAPPER.writeValue(versionFile, ruleset);

            // Overwrite latest file
            File latestFile = new File(CachePaths.getLatestCacheFile());
            MAPPER.writeValue(latestFile, ruleset);

        } catch (IOException e) {
            System.err.println("Failed to write rule cache: " + e.getMessage());
        }
    }

    private static void ensureDir() {
        File dir = new File(CachePaths.getCacheDir());
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
