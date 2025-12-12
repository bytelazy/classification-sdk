package com.example.policybackend.service;

import com.example.policybackend.model.Policy;
import com.example.policybackend.model.PolicyStatus;
import com.example.policybackend.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {
    private final PolicyRepository repository = new PolicyRepository();

    public Policy create(Policy policy) {
        return repository.save(policy);
    }

    public Optional<Policy> get(String id) {
        return repository.findById(id);
    }

    public List<Policy> list() {
        return repository.findAll();
    }

    public Optional<Policy> update(String id, Policy updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setLevel(updated.getLevel());
            existing.setPriority(updated.getPriority());
            existing.setPatterns(updated.getPatterns());
            return repository.save(existing);
        });
    }

    public boolean delete(String id) {
        boolean exists = repository.findById(id).isPresent();
        repository.delete(id);
        return exists;
    }

    public Optional<Policy> approve(String id) {
        return updateStatus(id, PolicyStatus.APPROVED);
    }

    public Optional<Policy> publish(String id) {
        return updateStatus(id, PolicyStatus.PUBLISHED);
    }

    public List<Policy> listPublished() {
        return repository.findByStatus(PolicyStatus.PUBLISHED);
    }

    private Optional<Policy> updateStatus(String id, PolicyStatus status) {
        return repository.findById(id).map(policy -> {
            policy.setStatus(status);
            return repository.save(policy);
        });
    }
}
