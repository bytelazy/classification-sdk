# Classification SDK & Policy Management Platform

This repository contains a proof‑of‑concept implementation of a
classification SDK and supporting services for identifying and managing
sensitive data.  It is designed as a teaching aid for building
enterprise‑grade data classification platforms similar to those used by
large technology companies (Google, AWS, Meta, etc.).

The project is comprised of several modules:

- **classification‑sdk** – A Java library that executes classification rules
  against input text.  The SDK includes a pluggable rule engine,
  configuration polling with ETag support, fuzzy matching
  capabilities and bootstrap rules for offline operation.
- **mock‑policy‑service** – A Spring Boot service that returns a
  JSON document containing rules.  It serves as a simple backend
  demonstrating how the SDK can fetch configuration from a remote
  service.  The service supports live reloading of its `rules.json` file.
- **policy‑backend** – A new module providing CRUD, approval and
  publishing endpoints for managing policies.  Policies are stored
  in memory for demonstration purposes.  This module can be wired
  into a configuration pipeline to produce the JSON consumed by
  the SDK.
- **docs** – Architecture diagrams, integration guides and design
  notes.  See below for details.

## Getting Started

This project uses Maven and Java 8.  Each module can be built
independently using `mvn package` from the module directory.  To run
the policy backend or mock service you can execute:

```bash
mvn spring-boot:run
```

from within the respective module directory.

The SDK itself can be used by adding a Maven dependency on
`classification‑sdk` and instantiating the `ClassificationSdk` class.
See `DemoMain.java` under `classification‑sdk/src/test/java` for a
minimal usage example.

## Documentation

The `docs` directory contains in‑depth documentation:

- `architecture.md` – High level overview of the system
  architecture, including sequence diagrams and deployment notes.
- `policy_backend.md` – Design of the policy management API and its
  workflow, including approval and publishing semantics.
- `integration_guide.md` – Instructions for integrating the SDK
  into a business application, including examples and best
  practices.
- `rule_engine.md` – Discussion of the rule engine internals, the
  available matcher types (regex, fuzzy) and guidance on adding
  custom matchers or machine learning models.

We encourage contributions and feedback.  Please see `CONTRIBUTING.md`
for guidelines on submitting patches and filing issues (this file
should be created as part of your own workflow if you intend to open
source the project).