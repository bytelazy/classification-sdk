package com.example.policybackend.controller;

import com.example.policybackend.model.Policy;
import com.example.policybackend.service.PolicyService;
import com.example.sdk.model.MatcherDef;
import com.example.sdk.model.Rule;
import com.example.sdk.model.Ruleset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
public class RulesController {
    private final PolicyService policyService;
    private final AtomicReference<Ruleset> lastRuleset = new AtomicReference<>();

    public RulesController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @GetMapping("/api/v1/rules")
    public ResponseEntity<Ruleset> fetchRules(@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
        Ruleset latest = buildRuleset();
        String eTag = computeETag(latest);

        Ruleset cached = lastRuleset.get();
        if (eTag.equals(ifNoneMatch) && cached != null) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        latest.setETag(eTag);
        lastRuleset.set(latest);

        HttpHeaders headers = new HttpHeaders();
        headers.setETag(eTag);
        return new ResponseEntity<>(latest, headers, HttpStatus.OK);
    }

    private Ruleset buildRuleset() {
        List<Policy> published = policyService.listPublished();
        List<Rule> rules = new ArrayList<>();

        for (Policy policy : published) {
            Rule rule = new Rule();
            rule.setId(policy.getId());
            rule.setName(policy.getName());
            rule.setLevel(policy.getLevel());
            rule.setPriority(policy.getPriority());
            rule.setEnabled(true);

            List<MatcherDef> matchers = new ArrayList<>();
            for (String pattern : policy.getPatterns()) {
                MatcherDef matcher = new MatcherDef();
                matcher.setType("regex");
                matcher.setPattern(pattern);
                matchers.add(matcher);
            }
            rule.setMatchers(matchers);
            rules.add(rule);
        }

        Ruleset ruleset = new Ruleset();
        ruleset.setRules(rules);
        ruleset.setVersion(calculateVersion(published));
        return ruleset;
    }

    private String computeETag(Ruleset ruleset) {
        String candidate = ruleset.getVersion() + "-" + ruleset.getRules().hashCode();
        return '"' + Integer.toHexString(candidate.hashCode()) + '"';
    }

    private String calculateVersion(List<Policy> published) {
        if (published.isEmpty()) {
            return "v-empty";
        }

        String seed = published.stream()
                .sorted((a, b) -> a.getId().compareToIgnoreCase(b.getId()))
                .map(p -> p.getId() + ":" + p.getName() + ":" + p.getLevel() + ":" + p.getPriority()
                        + ":" + String.join(",", p.getPatterns()))
                .collect(Collectors.joining("|"));
        return "v-" + Integer.toHexString(seed.hashCode());
    }
}
