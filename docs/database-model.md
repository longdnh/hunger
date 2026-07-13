# Database Model

## 1. Target database

Start with PostgreSQL and one shared schema. Put workspace_id on tenant-owned tables and design indexes around workspace-scoped queries.

Do not start with sharding or separate databases per tenant. First learn to make one database correct, observable, and performant.

## 2. Minimal data model

| Entity | Purpose |
| --- | --- |
| users | Global user identity |
| sessions | Login and refresh-token sessions |
| workspaces | Tenant records |
| memberships | User, workspace, role, and membership status |
| invitations | Secure workspace invitations |
| projects | Workspace projects |
| project_members | Optional restricted project access |
| tasks | Work items and current state |
| comments | Task discussions |
| attachments | File metadata and ownership |
| notifications | User notification inbox |
| activities | Workspace activity history |
| outbox_events | Reliable asynchronous events |

## 3. Modeling principles

### Tenant-first design

- Every workspace-owned record should have a direct workspace ownership path.
- Query patterns must always scope by workspace.
- A user should be able to belong to many workspaces.
- Access control must be enforced at the application boundary and supported by database constraints where possible.

### Referential integrity

- A project belongs to exactly one workspace.
- A task belongs to exactly one project and workspace.
- A comment and attachment belong to one task or comment context.
- Activity should remain append-only and should not be mutated after insertion.

### Isolation and correctness

- Completed tasks remain editable by authorized members, but edits are recorded.
- Deleting a project should initially mean archiving it, preserving historical data.
- Concurrent updates must not silently overwrite each other.

## 4. Recommended implementation notes

- Favor one shared PostgreSQL schema for the first milestone.
- Use workspace-scoped indexes for project listing, task listing, member lookup, and activity queries.
- Keep notification and outbox tables append-oriented so async behavior stays reliable.
- Normalize file metadata into attachments, while storing object references in external object storage.

## 5. Database quality goals

The initial database should be:

- correct under tenant isolation
- reproducible through migrations
- observable through logs and audit history
- performant for core workspace-scoped queries
- able to evolve without destructive schema changes
