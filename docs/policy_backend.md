# Policy Backend Design

This document describes the design of the policy management backend
implemented in the `policy‑backend` module.  The backend provides
RESTful endpoints for creating, updating, deleting, approving and
publishing classification policies.

## API Endpoints

All endpoints are rooted at `/api/policies`.

| Method & Path         | Description                                              |
|-----------------------|----------------------------------------------------------|
| `GET /api/policies`   | List all policies, regardless of status.                 |
| `GET /api/policies/{id}` | Retrieve a single policy by its id.                 |
| `POST /api/policies`  | Create a new policy.  Returns 201 Created.              |
| `PUT /api/policies/{id}` | Update an existing policy.  Returns 404 if not found. |
| `DELETE /api/policies/{id}` | Delete a policy.                                   |
| `POST /api/policies/{id}/approve` | Mark a policy as APPROVED.                 |
| `POST /api/policies/{id}/publish` | Mark a policy as PUBLISHED.               |
| `GET /api/policies/published` | List only published policies.                  |

### Data Model

The `Policy` model captures the minimal information needed for a
classification rule:

- **id**: Unique identifier (UUID) assigned by the backend.
- **name**: Human friendly name.
- **level**: Sensitivity level (e.g. L1, L2, L3).
- **priority**: Evaluation priority.  Higher numbers indicate
  earlier evaluation.
- **patterns**: List of regular expression strings used for
  matching.
- **status**: Lifecycle state (`DRAFT`, `APPROVED`, `PUBLISHED`).

### Workflow

1. A new policy is created via `POST /api/policies`.  The policy is
   initially in the `DRAFT` state.
2. When ready for review the policy can be updated as needed via
   `PUT /api/policies/{id}`.  Updates do not change the status.
3. Once reviewed and accepted an operator calls
   `POST /api/policies/{id}/approve` to move the policy to the
   `APPROVED` state.  At this point it is ready for publication.
4. Publishing is triggered via `POST /api/policies/{id}/publish`.
   This sets the status to `PUBLISHED`.  Published policies are
   eligible to be included in the rules JSON served to SDK clients.
5. The backend exposes `GET /api/policies/published` for the policy
   service to fetch published policies.  In a real deployment the
   backend would generate the aggregated rules document and upload
   it to the policy service.

### Extension Points

- **Persistence**: The current implementation stores policies in
  memory.  Replace the `PolicyRepository` with a database
  implementation (e.g. JPA repository) for durability.
- **Authentication & Authorization**: Use Spring Security or
  another framework to protect endpoints and enforce user roles
  (e.g. operator, reviewer, publisher).
- **Validation**: Add input validation to ensure that regular
  expressions compile and that policies meet business rules.
- **Auditing**: Record who created, approved and published a
  policy along with timestamps.