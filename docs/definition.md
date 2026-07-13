# Definition

## 1. Product definition

Hunger is a multi-tenant SaaS for small teams to manage projects and tasks.

The application has one simple purpose:

> A team creates a workspace, invites members, creates projects, assigns tasks, and follows work until completion.

Hunger is not intended to compete with large project-management products. It is a concrete application that provides enough real business behavior for learning backend engineering without requiring deep domain knowledge.

## 2. Core business scope

### Core concepts

- Workspace: the tenant and top-level data boundary.
- Member: a user who belongs to a workspace.
- Project: a container for related work inside a workspace.
- Task: a unit of work belonging to one project.
- Comment: a discussion item belonging to one task.
- Attachment: a file linked to one task or comment.
- Activity: an immutable record of an important user action.

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
- Status: TODO, IN_PROGRESS, or DONE
- Optional assignee
- Optional due date
- Priority: LOW, MEDIUM, or HIGH
- Created by and timestamps

Labels, subtasks, dependencies, recurring tasks, time tracking, and custom fields are later extensions, not initial requirements.

## 3. One core business flow

This is the product spine:

1. A user registers and signs in.
2. The user creates a workspace and becomes its owner.
3. The owner invites another user to the workspace.
4. The owner creates a project.
5. A member creates a task and assigns it to another member.
6. The assignee moves the task from TODO to IN_PROGRESS and then DONE.
7. Members discuss the task using comments and attachments.
8. Relevant members receive notifications and can inspect activity history.

Every engineering phase should improve this flow. A technology should not be added only to demonstrate that it can be installed.

## 4. Essential business rules

### Tenant isolation

- A user can belong to multiple workspaces.
- Every protected request operates in one explicit workspace context.
- Workspace-owned records include a workspace_id directly or have a strictly enforced ownership path.
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
