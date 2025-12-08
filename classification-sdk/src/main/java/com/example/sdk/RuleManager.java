package com.example.sdk;

import com.example.sdk.cache.CacheStore;
import com.example.sdk.config.SdkConfig;
import com.example.sdk.http.PolicyHttpClient;
import com.example.sdk.http.VersionedResponse;
import com.example.sdk.model.Ruleset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Central coordinator for loading, caching and updating classification rules.
 * It maintains a reference to the current ruleset and schedules periodic
 * refreshes from the remote policy service.
 */
public class RuleManager {

    private static final Logger log = LoggerFactory.getLogger(RuleManager.class);

    // Volatile ensures visibility across threads without requiring locks
    private static volatile Ruleset currentRuleset;

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final PolicyHttpClient httpClient = new PolicyHttpClient();

    /**
     * Initialize the rule manager. This method loads any cached rules from
     * disk or falls back to the bootstrap rules. It then performs an immediate
     * remote fetch and schedules periodic polling according to SdkConfig.
     */
    public static void init() {
        log.info("Initializing RuleManager ...");

        // 1) Attempt to load cached ruleset from disk
        Ruleset cached = CacheStore.loadLatest();
        if (cached != null) {
            log.info("Loaded cached ruleset: {}", cached.getVersion());
            currentRuleset = cached;
            SdkConfig.setLastETag(cached.getETag());
        } else {
            // 2) Otherwise, load embedded bootstrap rules
            log.warn("No local cache found. Loading bootstrap rules.");
            currentRuleset = BootstrapRulesLoader.load();
        }

        // 3) Always try to fetch fresh rules on startup
        fetchRemoteAndUpdate();

        // 4) Schedule periodic refresh at configured interval
        int interval = SdkConfig.getPollIntervalSeconds();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                fetchRemoteAndUpdate();
            } catch (Exception e) {
                log.error("Polling error: {}", e.getMessage());
            }
        }, interval, interval, TimeUnit.SECONDS);
    }

    /**
     * Perform a synchronous fetch from the remote policy service and update
     * both the in-memory ruleset and disk cache if new rules are returned.
     */
    private static void fetchRemoteAndUpdate() {
        VersionedResponse response = httpClient.fetchLatest();
        if (response == null) {
            return; // 304 or failure
        }

        Ruleset newRules = response.getRuleset();
        // Attach the ETag so it is persisted alongside the rules
        newRules.setETag(response.getETag());
        currentRuleset = newRules;

        SdkConfig.setLastETag(response.getETag());
        CacheStore.save(newRules);

        log.info("Ruleset updated and cached: {}", newRules.getVersion());
    }

    /**
     * Retrieve the currently active ruleset. This method returns null if
     * initialization has not occurred.
     */
    public static Ruleset getCurrentRuleset() {
        return currentRuleset;
    }
}