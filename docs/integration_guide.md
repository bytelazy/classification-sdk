# SDK Integration Guide

This guide explains how to integrate the `classification‑sdk` into
your application.  It covers dependency management, configuration
and usage patterns.

## Dependency

Add the SDK as a dependency in your Maven project.  You can do
this by publishing the `classification‑sdk` module to your local
repository (via `mvn install`) and then declaring it as a
dependency:

```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>classification-sdk</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Configuration

At runtime the SDK needs to know where to fetch its configuration
and how frequently to poll.  Configuration is provided via system
properties or environment variables:

| Property / Env Var              | Default                | Description                                         |
|--------------------------------|------------------------|-----------------------------------------------------|
| `sdk.policy.url` / `SDK_POLICY_URL` | `http://localhost:8080/api/v1/rules` | URL of the policy service endpoint.             |
| `sdk.rule.poll.interval.seconds` / `SDK_RULE_POLL_INTERVAL_SECONDS` | `900` (15 min) | Polling interval in seconds.                   |
| `sdk.rule.cache.dir` / `SDK_RULE_CACHE_DIR` | `~/.classification-sdk/` | Directory for cached rules.                   |

The SDK will load bootstrap rules from its JAR if no cached or
remote configuration is available.

## Usage

Instantiate the `ClassificationSdk` once at application startup.

```java
ClassificationSdk sdk = new ClassificationSdk();

// Default mode returns only the highest priority match per input.
DetectionResult result = sdk.classify("My phone number is 13812345678");
if (result.hasMatch()) {
    Rule matchedRule = result.getMatchedRules().get(0);
    System.out.println("Matched rule: " + matchedRule.getName());
}

// Optionally enable multi-match mode to return all matches.
DetectionResult allMatches = sdk.classify("ID: 123456789012345678", ClassificationMode.MULTI_MATCH_ALL);
```

## Best Practices

- **Reuse the SDK instance**: Creating multiple instances will spawn
  separate polling threads and consume unnecessary resources.  Share
  the instance across your application (e.g. using a singleton bean
  in Spring).
- **Handle updates**: The SDK automatically refreshes its
  configuration.  You can listen for rule hits by instrumenting
  logging around calls to `sdk.classify(...)`.
- **Tune polling**: Balance timeliness of rule updates with network
  overhead.  If your policies rarely change increase the polling
  interval.
- **Watch for errors**: The SDK falls back to cached or bootstrap
  rules when remote configuration fails.  Monitor your logs for
  repeated network errors indicating a problem with the policy
  service.