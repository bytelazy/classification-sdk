package com.example.sdkclientmock;

import com.example.sdk.ClassificationMode;
import com.example.sdk.ClassificationSdk;
import com.example.sdk.DetectionResult;
import com.example.sdk.model.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ClassificationSdkTests {

    @BeforeAll
    static void configurePolicySource() {
        // Point the SDK at an unreachable endpoint so tests rely on bootstrap rules
        System.setProperty("sdk.policy.url", "http://127.0.0.1:65535/api/v1/rules");
    }

    @Test
    @DisplayName("should detect phone number using bootstrap rules")
    void detectPhoneNumber() {
        ClassificationSdk sdk = new ClassificationSdk();

        DetectionResult result = sdk.classify("请联系我，号码是13812345678");

        printResult("phone only", result);

        assertThat(result.hasMatch()).isTrue();
        assertThat(result.getMatchedRules())
                .extracting(Rule::getId)
                .containsExactly("phone");
    }

    @Test
    @DisplayName("should surface highest priority rule in TOP_MATCH_ONLY mode")
    void detectHighestPriorityRule() {
        ClassificationSdk sdk = new ClassificationSdk();

        String text = "身份证:110105198001015678, 手机:13812345678";
        DetectionResult result = sdk.classify(text, ClassificationMode.TOP_MATCH_ONLY);

        printResult("top match", result);

        assertThat(result.hasMatch()).isTrue();
        assertThat(result.getMatchedRules())
                .extracting(Rule::getId)
                .containsExactly("idcard");
    }

    @Test
    @DisplayName("should return all matches when configured")
    void detectAllMatches() {
        ClassificationSdk sdk = new ClassificationSdk();

        String text = "我的身份证110105198001015678，手机号13812345678";
        DetectionResult result = sdk.classify(text, ClassificationMode.ALL_MATCHES);

        printResult("all matches", result);

        List<String> ids = result.getMatchedRules().stream().map(Rule::getId).collect(Collectors.toList());
        assertThat(ids).containsExactly("idcard", "phone");
    }

    private static void printResult(String label, DetectionResult result) {
        String matches = result.getMatchedRules().stream()
                .map(rule -> String.format("%s(%s)", rule.getId(), rule.getName()))
                .collect(Collectors.joining(", "));
        System.out.printf("[%s] input=%s, matched=%s, rules=[%s]%n",
                label, result.getData(), result.hasMatch(), matches);
    }
}
