# Hunger Lab - Simple Business Plan

## 1. Product idea

Hunger is a multi-tenant SaaS for small teams to manage projects and tasks.

The application has one simple purpose:

> A team creates a workspace, invites members, creates projects, assigns tasks, and follows work until completion.

Hunger is not intended to compete with large project-management products. It is a concrete application that provides enough real business behavior for learning backend engineering without requiring deep domain knowledge.

## 2. Why this business fits the lab

The core product is small, but each planned technology still solves a real problem:

| Technology | Product problem |
| --- | --- |
| Authentication | Users need secure accounts and sessions |
| Multi-tenancy | Workspaces must not see each other's data |
| Authorization | Owners, admins, members, and guests have different access |
| Database design | Projects, tasks, comments, and activity grow over time |
| File storage | Members attach files to tasks |
| Notifications | Members need assignment, mention, and deadline updates |
| Caching | Frequently opened project boards should load quickly |
| Performance | Large workspaces may contain many tasks and activities |
| Search | Users need to find tasks and comments |
| Event-driven | Activity, notification, search, and analytics can react to task events |
| Deployment | The application needs repeatable releases and monitoring |

## 3. Business scope

### Core concepts

- **Workspace:** The tenant and top-level data boundary.
- **Member:** A user who belongs to a workspace.
- **Project:** A container for related work inside a workspace.
- **Task:** A unit of work belonging to one project.
- **Comment:** A discussion item belonging to one task.
- **Attachment:** A file linked to one task or comment.
- **Activity:** An immutable record of an important user action.

### Roles

| Role | Capabilities |
| --- | --- |
| Owner | Manage workspace, billing, members, and all projects |
| Admin | Manage members and all projects, except ownership and billing |
| Member | Create and update work in projects they can access |
| Guest | Access only explicitly assigned projects or tasks |

Do not support custom roles in the first version. Fixed roles keep authorization understandable while still providing meaningful security work.

### Task fields

The first version of a task only needs:

- Title
- Optional description
- Status: `TODO`, `IN_PROGRESS`, or `DONE`
- Optional assignee
- Optional due date
- Priority: `LOW`, `MEDIUM`, or `HIGH`
- Created by and timestamps

Labels, subtasks, dependencies, recurring tasks, time tracking, and custom fields are later extensions, not initial requirements.

## 4. One core business flow

This is the product spine:

1. A user registers and signs in.
2. The user creates a workspace and becomes its owner.
3. The owner invites another user to the workspace.
4. The owner creates a project.
5. A member creates a task and assigns it to another member.
6. The assignee moves the task from `TODO` to `IN_PROGRESS` and then `DONE`.
7. Members discuss the task using comments and attachments.
8. Relevant members receive notifications and can inspect activity history.

Every engineering phase should improve this flow. A technology should not be added only to demonstrate that it can be installed.

## 5. Essential business rules

### Tenant isolation

- A user can belong to multiple workspaces.
- Every protected request operates in one explicit workspace context.
- Workspace-owned records include a `workspace_id` directly or have a strictly enforced ownership path.
- A member cannot read or change data from another workspace.
- Knowing a task or project ID never grants access to it.

### Membership

- A user needs an active membership to access a workspace.
- Only owners and admins can invite or remove members.
- The final owner cannot leave, be removed, or be demoted.
- Removing a member revokes access but preserves their historical work.
- A task can only be assigned to a member who can access its project.

### Projects and tasks

- A project belongs to exactly one workspace.
- A task belongs to exactly one project and workspace.
- Completed tasks remain editable by authorized members, but changes are recorded.
- Deleting a project should initially mean archiving it, preserving task history.
- Concurrent updates must not silently overwrite each other.

### Invitations

- An invitation has an expiry time and can only be accepted once.
- Accepting an invitation for the wrong email is rejected.
- Repeated acceptance requests do not create duplicate memberships.

## 6. Minimal data model

| Entity | Purpose |
| --- | --- |
| `users` | Global user identity |
| `sessions` | Login and refresh-token sessions |
| `workspaces` | Tenant records |
| `memberships` | User, workspace, role, and membership status |
| `invitations` | Secure workspace invitations |
| `projects` | Workspace projects |
| `project_members` | Optional restricted project access |
| `tasks` | Work items and current state |
| `comments` | Task discussions |
| `attachments` | File metadata and ownership |
| `notifications` | User notification inbox |
| `activities` | Workspace activity history |
| `outbox_events` | Reliable asynchronous events |

Start with PostgreSQL and one shared schema. Put `workspace_id` on tenant-owned tables and design indexes around workspace-scoped queries.

Do not start with sharding or separate databases per tenant. First learn to make one database correct, observable, and performant.

## 7. Engineering phases

The technical phases remain intentionally broad. The business behavior introduced in each phase stays small.

### Phase 0 - Foundation and guardrails

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
- Non-local environments do not use `ddl-auto=update`.

### Phase 1 - Authentication and workspace onboarding

**Product result:** A user can register, sign in, and create a workspace.

Build:

- Registration and email verification
- Login, logout, and password hashing
- Access token and refresh-token rotation
- Session revocation
- Workspace creation
- Workspace switching for users with multiple memberships
- Login throttling

Learn:

- Authentication versus authorization
- Session and JWT trade-offs
- Token rotation, CSRF, CORS, and brute-force protection

Done when:

- A revoked session cannot be reused.
- Reuse of an old refresh token is detected.
- Workspace context is explicit on protected requests.

### Phase 2 - Authorization and membership

**Product result:** An owner can invite people and control workspace access.

Build:

- Owner, admin, member, and guest roles
- Invite and accept flow
- Membership suspension and removal
- Project-level access for guests
- Audit activity for permission changes

Learn:

- RBAC and resource-scoped authorization
- Permission checks in the application layer
- Privilege escalation risks
- Parameterized security tests

Done when:

- A permission matrix is covered by tests.
- Cross-workspace access attempts fail.
- The final-owner rule is concurrency-safe.

### Phase 3 - Projects, tasks, comments, and files

**Product result:** A team can perform the complete core workflow.

Build:

- Project create, update, archive, and list
- Task create, assign, update status, and list
- Comments and activity history
- S3-compatible object storage for attachments
- Presigned upload and download
- File ownership, size, and content-type validation

Learn:

- Domain boundaries and database constraints
- Pagination and filtering
- Object storage versus database blobs
- Secure file handling

Done when:

- The core business flow works end to end.
- Files cannot be accessed across workspaces.
- Project archival preserves its history.

### Phase 4 - Reliable task workflow

**Product result:** Task updates remain correct under retries and concurrent users.

Build:

- Explicit task status transitions
- Optimistic locking
- Idempotency keys for important write requests
- Activity records for task changes
- Due-date and overdue calculation

Learn:

- Transactions and isolation levels
- Race conditions and lost updates
- Idempotent API design
- Immutable history

Done when:

- Duplicate requests create one business effect.
- Concurrent updates cannot silently overwrite each other.
- Activity history reflects successful changes only.

### Phase 5 - Notifications and asynchronous jobs

**Product result:** Members receive assignment, mention, comment, and deadline notifications.

Build:

- In-app notification inbox
- Email notifications
- Notification preferences
- Background jobs
- Transactional outbox
- Retry, backoff, and dead-letter handling
- Optional live updates using Server-Sent Events or WebSocket

Learn:

- At-least-once delivery
- Idempotent consumers
- Outbox pattern and eventual consistency
- Realtime connection management

Done when:

- A committed task event is eventually delivered after process failure.
- Duplicate events do not create duplicate notifications.
- Failed jobs can be inspected and retried.

### Phase 6 - Caching and performance

**Product result:** Project boards remain responsive in large workspaces.

Build:

- Redis cache for workspace settings and suitable project summaries
- Workspace-aware cache keys
- Cache invalidation after changes
- Rate limiting
- Query profiling and slow-query logging
- Load tests for project listing and task updates

Learn:

- Cache-aside, TTL, invalidation, and cache stampede
- Database indexes and query plans
- p50, p95, and p99 latency
- Connection-pool tuning

Done when:

- Baseline and optimized load-test results are recorded.
- Authorization never depends on stale cached permissions.
- Performance improvements are supported by measurements.

### Phase 7 - Search and reporting

**Product result:** Users can find work and view simple workspace statistics.

Build:

- PostgreSQL search for the first version
- Task and comment search
- Filters by project, status, assignee, and due date
- Simple counts for open, overdue, and completed tasks
- Elasticsearch or OpenSearch as a later experiment
- Reindexing and index-version strategy

Learn:

- Full-text search and ranking
- Tenant-filtered search
- Search indexes as rebuildable projections
- OLTP versus reporting workloads

Done when:

- Every query and search result is workspace-scoped.
- The external index can be rebuilt from PostgreSQL.
- Search indexing failures and lag are observable.

### Phase 8 - Event-driven architecture

**Product result:** Notifications, search, activity, and reporting can react independently to task changes.

Build:

- Versioned task domain events
- Kafka or RabbitMQ experiment
- Notification, search, and reporting consumers
- Retry and dead-letter flows
- Contract compatibility tests
- Event replay experiment

Learn:

- Event choreography and orchestration
- Ordering, partition keys, replay, and schema evolution
- Consumer idempotency

Done when:

- Replaying events does not corrupt projections.
- Duplicate delivery produces one logical effect.
- One task change can be traced across producers and consumers.

Keep Hunger as a modular monolith until there is evidence that extracting a service improves ownership, scaling, or reliability. A message broker does not require microservices.

### Phase 9 - Deployment, reliability, and security

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

Learn:

- Deployment strategies
- SLI, SLO, and error budgets
- RPO, RTO, incident response, and capacity planning
- OWASP practices and threat modeling

Done when:

- A restore drill meets the documented target.
- Deployment failure has a tested recovery path.
- Multiple application instances preserve correctness.

### Phase 10 - SaaS plans and platform operations

**Product result:** Workspaces can use plans with clear limits.

Build:

- Free and paid plans
- Limits for members, projects, and storage
- Centralized entitlements
- Usage metering
- Billing-provider webhook experiment
- Workspace export and suspension
- Audited platform-support access

Learn:

- Entitlement and quota modeling
- Webhook security and idempotency
- Reconciliation
- Data retention and support boundaries

Done when:

- Plan rules are centralized and tested.
- Duplicate webhooks do not duplicate business effects.
- Support access is explicit and audited.

## 8. Suggested module boundaries

Start as a modular monolith:

```text
com.engineering_lab.hunger
  identity
  workspace
  access
  project
  task
  notification
  file
  activity
  shared
```

Rules:

- One module cannot access another module's repository directly.
- Modules communicate through application services or explicit interfaces.
- API models are separate from persistence and domain models.
- `shared` contains technical primitives, not homeless business logic.
- Use in-process events first and durable events only when reliability requires them.

## 9. Database learning path

Use the same business data while increasing database depth:

1. Tables, foreign keys, unique constraints, and check constraints
2. Workspace-scoped compound indexes
3. Transactions and optimistic locking
4. Keyset pagination for tasks and activities
5. Query plans and slow-query analysis
6. Connection-pool and batch-operation tuning
7. Partitioning the append-heavy `activities` table as an experiment
8. Read replicas for reporting as an experiment
9. Sharding only after simulating and measuring a real limit

Important query examples:

- List tasks in a workspace project by status and creation time.
- List tasks assigned to the current user across projects.
- Find overdue tasks without scanning unrelated workspaces.
- Page through workspace activity without large offsets.
- Check whether uniqueness constraints correctly include `workspace_id`.

## 10. First four iterations

### Iteration 1 - Database foundation

- Add Flyway.
- Replace `ddl-auto=update`.
- Create `users`, `workspaces`, and `memberships`.
- Define ID, timestamp, audit, and deactivation conventions.
- Add PostgreSQL integration tests with Testcontainers.

### Iteration 2 - Authentication

- Register and verify email.
- Login, refresh, logout, and revoke sessions.
- Add login throttling and security logs.
- Test token replay and invalid credentials.

### Iteration 3 - Workspace isolation

- Create and switch workspaces.
- Invite and accept members.
- Resolve workspace context for protected requests.
- Build cross-workspace attack tests.

### Iteration 4 - The first usable product

- Create a project.
- Create, assign, list, and update tasks.
- Enforce fixed roles.
- Add the first activity records.

After Iteration 4, Hunger is already a small usable application. Later phases improve the same product instead of replacing its business model.

## 11. Learning loop

For each feature:

1. Describe one user problem.
2. Write the business rules and security risks.
3. Implement the smallest end-to-end slice.
4. Add tests for success, failure, and cross-workspace access.
5. Measure queries or performance where relevant.
6. Record the design decision and what was learned.

Suggested documentation structure:

```text
docs/
  business-plan.md
  architecture/
    decisions/
  milestones/
  experiments/
```

## 12. Definition of done

A milestone is complete when:

- Its user flow works end to end.
- Workspace isolation and authorization have negative tests.
- Schema changes are represented by migrations.
- Important rules are enforced by code and database constraints where suitable.
- Logs contain correlation and workspace context without secrets.
- Failure and retry behavior is understood.
- Relevant performance has a recorded baseline.
- Trade-offs and deferred ideas are documented.

## 13. Complexity budget

Use these constraints to keep the lab focused:

- One core aggregate at first: Task.
- One primary database: PostgreSQL.
- One deployable application until a measured need proves otherwise.
- Four fixed workspace roles.
- Three task statuses and three priority values.
- No custom workflow, custom fields, subtasks, or task dependencies initially.
- No Kafka, Elasticsearch, Redis, or Kubernetes before the phase that gives each one a concrete problem.
- Every new feature must support the core flow or a specific engineering experiment.

The goal is not to build the largest product. The goal is to revisit one understandable product at increasing levels of engineering depth.
