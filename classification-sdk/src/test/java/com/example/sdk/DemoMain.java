package com.example.sdk;

/**
 * Simple demonstration of the SDK in action. Runs a loop that classifies a
 * hardcoded string every 10 seconds, printing the name of any matched rule.
 * As new policies are deployed to the mock policy service the output will
 * update without restarting this application.
 */
public class DemoMain {

    public static void main(String[] args) throws Exception {
        ClassificationSdk sdk = new ClassificationSdk();

        while (true) {
            DetectionResult result = sdk.classify("李四 13812345678");
            if (result.hasMatch()) {
                System.out.println("Match: "
                        + result.getMatchedRules().get(0).getName());
            } else {
                System.out.println("No match");
            }
            Thread.sleep(10000); // 10 秒检查一次
        }
    }
}
