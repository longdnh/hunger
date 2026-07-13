# Iteration

This document turns the broad engineering phases into a practical iterative delivery plan.

## Iteration 0 — foundation

**Goal:** Make the project easy to run, test, and evolve.

**Deliverables:**

- PostgreSQL local environment
- Flyway migrations
- environment-based configuration
- standard error handling
- structured logging and correlation IDs
- basic CI and test conventions

## Iteration 1 — authentication and workspace creation

**Goal:** Allow a real user to register, sign in, and establish a tenant.

**Deliverables:**

- registration and email verification
- password hashing and session strategy
- refresh-token rotation
- workspace creation
- workspace switch behavior for multi-membership users

## Iteration 2 — authorization and membership

**Goal:** Make access control explicit and safe across tenants.

**Deliverables:**

- owner/admin/member/guest roles
- invitation and acceptance flow
- suspension and removal of members
- project-level guest access
- audit trail for permission changes

## Iteration 3 — core project workflow

**Goal:** Deliver the main business value in one end-to-end flow.

**Deliverables:**

- project management
- task CRUD and assignment
- comments and activity history
- attachment uploads and ownership checks

## Iteration 4 — reliable task updates

**Goal:** Ensure persistent correctness when multiple users edit the same task.

**Deliverables:**

- explicit status transitions
- optimistic locking
- idempotency for critical writes
- activity records for every successful mutation

## Iteration 5 — notifications and async workers

**Goal:** Support real-time user awareness without breaking consistency.

**Deliverables:**

- in-app notification inbox
- email notifications
- transactional outbox
- retry and dead-letter handling
- optional live updates

## Iteration 6 — performance and caching

**Goal:** Keep project boards responsive when workspace size grows.

**Deliverables:**

- Redis-backed caching for reads
- workspace-aware cache keys
- cache invalidation strategy
- load-testing and query profiling

## Iteration 7 — search and reporting

**Goal:** Make work easy to find and measure.

**Deliverables:**

- workspace-scoped search
- filters for project, status, assignee, and due date
- simple reporting counts
- rebuildable search indexing strategy

## Iteration 8 — event-driven decoupling

**Goal:** Let notifications, search, and reporting react to changes independently.

**Deliverables:**

- versioned domain events
- broker experiment
- replayable consumers
- contract compatibility tests

## Iteration 9 — deployment and reliability

**Goal:** Make the product safe to ship and recover.

**Deliverables:**

- containerization and deployment pipeline
- health, readiness, metrics, and tracing
- backup and restore procedure
- security scanning and resilience checks

## Recommended release shape

A practical release sequence is:

1. Foundation
2. Authentication and workspace onboarding
3. Authorization and membership
4. Projects, tasks, comments, and files
5. Reliability and notifications
6. Performance and search
7. Event-driven and operational maturity

This keeps the product incrementally useful while still leaving room for the learning goals of each engineering milestone.
