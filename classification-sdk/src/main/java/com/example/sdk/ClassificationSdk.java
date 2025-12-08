package com.example.sdk;

/**
 * Public facing API entry point for performing data classification. Construct
 * this class once per application instance. On creation it initializes the
 * RuleManager which loads rules and schedules periodic refreshes. Client
 * applications then call classify to inspect strings for sensitive content.
 */
public class ClassificationSdk {

    private final DetectionEngine engine = new DetectionEngine();

    public ClassificationSdk() {
        RuleManager.init();
    }

    /**
     * Classify the given data and return a detection result using the default
     * mode (TOP_MATCH_ONLY).
     */
    public DetectionResult classify(String data) {
        return classify(data, ClassificationMode.TOP_MATCH_ONLY);
    }

    /**
     * Classify the given data and return a detection result according to the
     * specified detection mode.
     */
    public DetectionResult classify(String data, ClassificationMode mode) {
        return engine.detect(data, mode);
    }
}