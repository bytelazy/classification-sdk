package com.example.sdk.http;

import com.example.sdk.model.Ruleset;

/**
 * Simple wrapper object containing a ruleset and the ETag header returned
 * alongside it. Used by the HTTP client to convey both pieces of information
 * back to the RuleManager.
 */
public class VersionedResponse {
    private final Ruleset ruleset;
    private final String eTag;

    public VersionedResponse(Ruleset ruleset, String eTag) {
        this.ruleset = ruleset;
        this.eTag = eTag;
    }

    public Ruleset getRuleset() { return ruleset; }
    public String getETag() { return eTag; }
}