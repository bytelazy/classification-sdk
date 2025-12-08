package com.example.sdk.model;

import java.util.List;

/**
 * A Rule represents a single classification rule in the system. Rules are
 * configured via the external policy service or bootstrap configuration and
 * define the priority and set of matchers used to detect sensitive values. A
 * higher priority value means the rule is preferred if multiple rules match
 * the same piece of data. Rules can be enabled or disabled individually.
 */
public class Rule {
    private String id;
    private String name;
    private String level;
    private Integer priority;
    private List<MatcherDef> matchers;
    private boolean enabled;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public List<MatcherDef> getMatchers() { return matchers; }
    public void setMatchers(List<MatcherDef> matchers) { this.matchers = matchers; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
