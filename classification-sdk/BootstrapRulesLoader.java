package com.example.sdk;

import com.example.sdk.model.Ruleset;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Responsible for loading a built-in set of rules that ships with the SDK. These
 * bootstrap rules are used when no cached policy exists on disk and the
 * remote configuration service cannot be reached (e.g. cold start, offline).
 */
public class BootstrapRulesLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Load the bootstrap rules bundled with the jar. This method is called once
     * during initialization if no cached rules are present.
     *
     * @return the parsed Ruleset from the bundled JSON
     */
    public static Ruleset load() {
        try (InputStream is = BootstrapRulesLoader.class.getClassLoader()
                .getResourceAsStream("bootstrap-rules.json")) {

            if (is == null) {
                throw new IllegalStateException("bootstrap-rules.json not found");
            }

            return MAPPER.readValue(is, Ruleset.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load bootstrap rules", e);
        }
    }
}