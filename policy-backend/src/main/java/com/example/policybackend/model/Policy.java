package com.example.policybackend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Policy {
    private String id;
    private String name;
    private String level;
    private Integer priority;
    private List<String> patterns = new ArrayList<>();
    private PolicyStatus status = PolicyStatus.DRAFT;

    public Policy() {
        this.id = UUID.randomUUID().toString();
    }

    public Policy(String id, String name, String level, Integer priority, List<String> patterns, PolicyStatus status) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.priority = priority;
        this.patterns = patterns != null ? patterns : new ArrayList<>();
        this.status = status != null ? status : PolicyStatus.DRAFT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns != null ? patterns : new ArrayList<>();
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }
}
