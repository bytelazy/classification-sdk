# Architecture Overview

This document provides a high‑level overview of the components in
this repository and how they interact.  It serves as a starting
point for discussions and design reviews.

## Components

### classification‑sdk

The SDK is a lightweight library that applications embed to perform
sensitive data classification.  Key features include:

- **Rule Engine** – Executes regular expression and fuzzy matching
  rules to detect personally identifiable information.  The engine
  can be extended to support additional matcher types, such as
  dictionaries or machine learning models.
- **Dynamic Configuration** – Fetches rule definitions from a
  backend service using conditional HTTP requests (ETag).  This
  allows operations teams to roll out new rules without requiring
  application redeployments.
- **Offline Bootstrap** – Ships with a set of bootstrap rules so
  that the SDK can operate even when no configuration is available.

### mock‑policy‑service

This service hosts a JSON file containing rule definitions.  It
exposes a single `/api/v1/rules` endpoint with ETag support.  The
service watches the underlying `rules.json` file for changes and
updates the ETag accordingly.  While simplistic, it illustrates the
contract between the SDK and a policy distribution service.

### policy‑backend

The policy backend provides CRUD operations, approval and publishing
workflows for policies.  It is implemented as a REST API using
Spring Boot.  Policies are stored in memory for demonstration
purposes.  In a production system you would persist policies to a
database and add proper authentication and auditing.

## Data Flow

1. **Policy Authoring** – Operators use the policy backend to
   create and edit policies.  Policies progress through DRAFT,
   APPROVED and PUBLISHED states.
2. **Publishing** – When a policy is marked PUBLISHED the backend
   can generate an aggregated rules document (not implemented here
   but straightforward) and push it to the policy service.
3. **SDK Polling** – The classification SDK periodically pulls the
   rules document from the policy service.  It uses the ETag header
   to avoid downloading unchanged content.
4. **Classification Execution** – Applications call the SDK with
   input data.  The SDK executes the rule engine and returns
   classification results.  If multiple rules match the SDK either
   returns the highest priority match or all matches depending on
   the configured `ClassificationMode`.

Below is a simplified sequence diagram of the typical workflow:

```
User/Operator -> PolicyBackend : Create/Update Policy (JSON)
PolicyBackend -> PolicyBackend : Validate & Store Policy
User/Operator -> PolicyBackend : Approve Policy
User/Operator -> PolicyBackend : Publish Policy
PolicyBackend -> PolicyService : (optional) Upload aggregated rules JSON
ClassificationSDK -> PolicyService : GET /api/v1/rules (If-None-Match: ETag)
PolicyService -> ClassificationSDK : 200 OK (with rules) or 304 Not Modified
Application -> ClassificationSDK : classify(data)
ClassificationSDK -> RuleEngine : evaluate matchers
RuleEngine -> Application : return classification result
```

## Deployment Considerations

- In production, the policy backend and policy service should be
  separate components.  The backend would manage policy lifecycles,
  while the service focuses on delivering the current published
  policies to clients.
- High availability and caching layers should be added to the
  policy service to support a large number of SDK clients.
- Authentication and authorization are critical for the backend to
  ensure only authorized users can modify or publish policies.
- Auditing changes to policies and their approvals is essential for
  compliance and troubleshooting.
