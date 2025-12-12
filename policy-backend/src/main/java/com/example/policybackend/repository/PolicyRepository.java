package com.example.policybackend.repository;

import com.example.policybackend.model.Policy;
import com.example.policybackend.model.PolicyStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PolicyRepository {
    private final Map<String, Policy> storage = new ConcurrentHashMap<>();

    public Policy save(Policy policy) {
        storage.put(policy.getId(), policy);
        return policy;
    }

    public Optional<Policy> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Policy> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(String id) {
        storage.remove(id);
    }

    public List<Policy> findByStatus(PolicyStatus status) {
        return storage.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }
}
