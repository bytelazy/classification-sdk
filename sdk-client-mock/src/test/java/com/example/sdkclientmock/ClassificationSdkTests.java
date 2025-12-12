package com.example.sdkclientmock;

import com.example.sdk.ClassificationMode;
import com.example.sdk.ClassificationSdk;
import com.example.sdk.DetectionResult;
import com.example.sdk.model.Rule;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lightweight test harness that can run without external libraries. Execute the
 * main method to run the assertions; a non-zero exit indicates failure.
 */
public class ClassificationSdkTests {

    public static void main(String[] args) {
        ClassificationSdkTests tests = new ClassificationSdkTests();
        tests.configurePolicySource();
        tests.detectPhoneNumber();
        tests.detectHighestPriorityRule();
        tests.detectAllMatches();
        System.out.println("All sdk-client-mock tests passed.");
    }

    private void configurePolicySource() {
        System.setProperty("sdk.policy.url", "http://127.0.0.1:65535/api/v1/rules");
    }

    private void detectPhoneNumber() {
        ClassificationSdk sdk = new ClassificationSdk();
        DetectionResult result = sdk.classify("请联系我，号码是13812345678");
        printResult("phone only", result);
        assertTrue(result.hasMatch(), "Expected phone number match");
        assertIds(result.getMatchedRules(), Arrays.asList("phone"));
    }

    private void detectHighestPriorityRule() {
        ClassificationSdk sdk = new ClassificationSdk();
        String text = "身份证:110105198001015678, 手机:13812345678";
        DetectionResult result = sdk.classify(text, ClassificationMode.TOP_MATCH_ONLY);
        printResult("top match", result);
        assertTrue(result.hasMatch(), "Expected at least one match");
        assertIds(result.getMatchedRules(), Arrays.asList("idcard"));
    }

    private void detectAllMatches() {
        ClassificationSdk sdk = new ClassificationSdk();
        String text = "我的身份证110105198001015678，手机号13812345678";
        DetectionResult result = sdk.classify(text, ClassificationMode.ALL_MATCHES);
        printResult("all matches", result);
        assertIds(result.getMatchedRules(), Arrays.asList("idcard", "phone"));
    }

    private void printResult(String label, DetectionResult result) {
        String matches = result.getMatchedRules().stream()
                .map(rule -> String.format("%s(%s)", rule.getId(), rule.getName()))
                .collect(Collectors.joining(", "));
        System.out.printf("[%s] input=%s, matched=%s, rules=[%s]%n",
                label, result.getData(), result.hasMatch(), matches);
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void assertIds(List<Rule> rules, List<String> expected) {
        List<String> ids = rules.stream().map(Rule::getId).collect(Collectors.toList());
        if (!ids.equals(expected)) {
            throw new AssertionError("Expected ids " + expected + " but got " + ids);
        }
    }
}
