# Phase

## 1. Phase 0 - Foundation and guardrails

**Product result:** The application starts reliably and database changes are reproducible.

Build:

- PostgreSQL local environment
- Flyway database migrations
- Environment-based configuration
- Standard API error format
- Request correlation ID and structured logging
- Unit and integration test conventions
- Basic CI pipeline

Learn:

- Spring Boot configuration and profiles
- Database migration discipline
- Testcontainers
- Logging and API error design

Done when:

- A new database can be created from migrations alone.
- CI runs the test suite.
- Non-local environments do not use ddl-auto=update.

## 2. Phase 1 - Authentication and workspace onboarding

**Product result:** A user can register, sign in, and create a workspace.

Build:

- Registration and email verification
- Login, logout, and password hashing
- Access token and refresh-token rotation
- Session revocation
- Workspace creation
- Workspace switching for users with multiple memberships
- Login throttling

Done when:

- A revoked session cannot be reused.
- Reuse of an old refresh token is detected.
- Workspace context is explicit on protected requests.

## 3. Phase 2 - Authorization and membership

**Product result:** An owner can invite people and control workspace access.

Build:

- Owner, admin, member, and guest roles
- Invite and accept flow
- Membership suspension and removal
- Project-level access for guests
- Audit activity for permission changes

Done when:

- A permission matrix is covered by tests.
- Cross-workspace access attempts fail.
- The final-owner rule is concurrency-safe.

## 4. Phase 3 - Projects, tasks, comments, and files

**Product result:** A team can perform the complete core workflow.

Build:

- Project create, update, archive, and list
- Task create, assign, update status, and list
- Comments and activity history
- S3-compatible object storage for attachments
- Presigned upload and download
- File ownership, size, and content-type validation

Done when:

- The core business flow works end to end.
- Files cannot be accessed across workspaces.
- Project archival preserves its history.

## 5. Phase 4 - Reliable task workflow

**Product result:** Task updates remain correct under retries and concurrent users.

Build:

- Explicit task status transitions
- Optimistic locking
- Idempotency keys for important write requests
- Activity records for task changes
- Due-date and overdue calculation

Done when:

- Duplicate requests create one business effect.
- Concurrent updates cannot silently overwrite each other.
- Activity history reflects successful changes only.

## 6. Phase 5 - Notifications and asynchronous jobs

**Product result:** Members receive assignment, mention, comment, and deadline notifications.

Build:

- In-app notification inbox
- Email notifications
- Notification preferences
- Background jobs
- Transactional outbox
- Retry, backoff, and dead-letter handling
- Optional live updates using Server-Sent Events or WebSocket

Done when:

- A committed task event is eventually delivered after process failure.
- Duplicate events do not create duplicate notifications.
- Failed jobs can be inspected and retried.

## 7. Phase 6 - Caching and performance

**Product result:** Project boards remain responsive in large workspaces.

Build:

- Redis cache for workspace settings and suitable project summaries
- Workspace-aware cache keys
- Cache invalidation after changes
- Rate limiting
- Query profiling and slow-query logging
- Load tests for project listing and task updates

Done when:

- Baseline and optimized load-test results are recorded.
- Authorization never depends on stale cached permissions.
- Performance improvements are supported by measurements.

## 8. Phase 7 - Search and reporting

**Product result:** Users can find work and view simple workspace statistics.

Build:

- PostgreSQL search for the first version
- Task and comment search
- Filters by project, status, assignee, and due date
- Simple counts for open, overdue, and completed tasks
- Elasticsearch or OpenSearch as a later experiment
- Reindexing and index-version strategy

Done when:

- Every query and search result is workspace-scoped.
- The external index can be rebuilt from PostgreSQL.
- Search indexing failures and lag are observable.

## 9. Phase 8 - Event-driven architecture

**Product result:** Notifications, search, activity, and reporting can react independently to task changes.

Build:

- Versioned task domain events
- Kafka or RabbitMQ experiment
- Notification, search, and reporting consumers
- Retry and dead-letter flows
- Contract compatibility tests
- Event replay experiment

Done when:

- Replaying events does not corrupt projections.
- Duplicate delivery produces one logical effect.
- One task change can be traced across producers and consumers.

Keep Hunger as a modular monolith until there is evidence that extracting a service improves ownership, scaling, or reliability. A message broker does not require microservices.

## 10. Phase 9 - Deployment, reliability, and security

**Product result:** Hunger can be deployed, observed, upgraded, and restored safely.

Build:

- Container image and deployment environments
- CI/CD pipeline
- Infrastructure as code
- Health and readiness checks
- Metrics, logs, traces, dashboards, and alerts
- Backup and restore procedure
- Dependency and container scanning
- Horizontal-scaling experiment

Done when:

- A restore drill meets the documented target.
