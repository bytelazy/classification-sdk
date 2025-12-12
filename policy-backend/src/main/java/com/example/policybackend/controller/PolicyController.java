package com.example.policybackend.controller;

import com.example.policybackend.model.Policy;
import com.example.policybackend.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {
    private final PolicyService service;

    public PolicyController(PolicyService service) {
        this.service = service;
    }

    @GetMapping
    public List<Policy> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> get(@PathVariable String id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Policy> create(@RequestBody Policy policy) {
        Policy created = service.create(policy);
        return ResponseEntity.created(URI.create("/api/policies/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> update(@PathVariable String id, @RequestBody Policy policy) {
        return service.update(id, policy)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean existed = service.delete(id);
        return existed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Policy> approve(@PathVariable String id) {
        return service.approve(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Policy> publish(@PathVariable String id) {
        return service.publish(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/published")
    public List<Policy> listPublished() {
        return service.listPublished();
    }
}
