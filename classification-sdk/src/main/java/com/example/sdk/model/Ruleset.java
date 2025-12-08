package com.example.sdk.model;

import java.util.List;

/**
 * A collection of rules delivered from the policy service. The Ruleset carries
 * a version for traceability and an optional ETag which is used to optimize
 * network fetches. When persisted to disk the ETag is saved so that the next
 * run can start by issuing a conditional request.
 */
public class Ruleset {
    private String version;
    private List<Rule> rules;
    private String eTag;

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<Rule> getRules() { return rules; }
    public void setRules(List<Rule> rules) { this.rules = rules; }

    public String getETag() { return eTag; }
    public void setETag(String eTag) { this.eTag = eTag; }
}
