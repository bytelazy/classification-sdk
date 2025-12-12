package com.example.sdkclientmock.controller;

import com.example.sdk.ClassificationMode;
import com.example.sdk.ClassificationSdk;
import com.example.sdk.DetectionResult;
import com.example.sdk.model.Rule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classify")
public class ClassificationController {

    private final ClassificationSdk sdk = new ClassificationSdk();

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("sdk-client-mock is running");
    }

    @PostMapping
    public ResponseEntity<ClassificationResponse> classify(@RequestBody ClassificationRequest request) {
        if (request == null || request.getText() == null) {
            return ResponseEntity.badRequest().build();
        }

        ClassificationMode mode = request.getMode() != null
                ? request.getMode()
                : ClassificationMode.TOP_MATCH_ONLY;

        DetectionResult result = sdk.classify(request.getText(), mode);
        ClassificationResponse response = new ClassificationResponse();
        response.setInput(request.getText());
        response.setMatched(result.hasMatch());
        response.setRules(result.getMatchedRules());
        return ResponseEntity.ok(response);
    }

    public static class ClassificationRequest {
        private String text;
        private ClassificationMode mode;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ClassificationMode getMode() {
            return mode;
        }

        public void setMode(ClassificationMode mode) {
            this.mode = mode;
        }
    }

    public static class ClassificationResponse {
        private String input;
        private boolean matched;
        private List<Rule> rules;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }

        public List<Rule> getRules() {
            return rules;
        }

        public void setRules(List<Rule> rules) {
            this.rules = rules;
        }
    }
}
