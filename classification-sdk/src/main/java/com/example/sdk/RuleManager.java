package com.example.sdk;

import com.example.sdk.model.MatcherDef;
import com.example.sdk.model.Rule;
import com.example.sdk.model.Ruleset;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Loads and exposes the currently active ruleset. In this offline-friendly
 * variant we avoid all network calls and simply bootstrap a small set of
 * built-in rules so the SDK can function without external dependencies.
 */
public class RuleManager {

    private static volatile Ruleset currentRuleset;

    /**
     * Initialize the rule manager. This method is idempotent and safe to call
     * multiple times. It eagerly loads a static set of bootstrap rules that
     * mirror the contents of the original JSON resource.
     */
    public static synchronized void init() {
        if (currentRuleset != null) {
            return;
        }
        currentRuleset = buildBootstrapRules();
    }

    /**
     * Retrieve the currently active ruleset.
     */
    public static Ruleset getCurrentRuleset() {
        return currentRuleset;
    }

    private static Ruleset buildBootstrapRules() {
        Rule phoneRule = new Rule();
        phoneRule.setId("phone");
        phoneRule.setName("手机号");
        phoneRule.setLevel("L2");
        phoneRule.setPriority(10);
        phoneRule.setMatchers(Collections.singletonList(new MatcherDef("regex", "\\b1[3-9]\\d{9}\\b")));
        phoneRule.setEnabled(true);

        Rule idCardRule = new Rule();
        idCardRule.setId("idcard");
        idCardRule.setName("身份证号");
        idCardRule.setLevel("L3");
        idCardRule.setPriority(20);
        idCardRule.setMatchers(Collections.singletonList(new MatcherDef("regex", "\\b\\d{17}[0-9Xx]\\b")));
        idCardRule.setEnabled(true);

        List<Rule> rules = Arrays.asList(phoneRule, idCardRule);
        Ruleset ruleset = new Ruleset();
        ruleset.setVersion("bootstrap-1.0");
        ruleset.setRules(rules);
        return ruleset;
    }
}
