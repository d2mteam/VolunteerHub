# Resource Graph Permission Model

## Problem Statement
VolunteerHub needs fine-grained authorization for a resource graph that follows the relationships `event -> post -> comment -> like`. The system must capture ownership, support sensible defaults, and resolve inherited permissions efficiently with PostgreSQL and JPA.

## Requirements
- Permissions are resolved along the resource graph: child nodes inherit grants from their ancestors unless explicitly overridden.
- Ownership is recorded at creation time to support defaults (e.g., creators can manage what they create).
- Queries must remain simple enough for PostgreSQL recursive CTEs and JPA mapping.
- Soft-deletes should keep the graph and permissions consistent.

## Conceptual Model
- **Resource Node**: Represents a single entity (Event, Post, Comment, Like). Includes type, status, and timestamps.
- **Resource Edge**: Directed link describing parent-child relationships in the graph (e.g., Event -> Post).
- **Owner**: The user who created the node; used for default grants.
- **Permission Grant**: A mapping of subject (User or Role) to a permission set on a node.

### Permission Set (enum suggestion)
- `VIEW`
- `COMMENT`
- `POST`
- `MODERATE`
- `ADMIN` (full control)

## Data Model
### Tables
1. **resource_node**
   - `id` (UUID/Snowflake, PK)
   - `type` (enum: `EVENT`, `POST`, `COMMENT`, `LIKE`)
   - `owner_id` (UUID, FK -> users)
   - `status` (enum: `ACTIVE`, `SOFT_DELETED`)
   - `created_at`, `updated_at`

2. **resource_edge**
   - `parent_id` (FK -> resource_node.id)
   - `child_id` (FK -> resource_node.id)
   - `constraint` (FK) to enforce uniqueness `(parent_id, child_id)`

3. **permission_grant**
   - `id` (PK)
   - `resource_id` (FK -> resource_node.id)
   - `subject_type` (enum: `USER`, `ROLE`)
   - `subject_id` (UUID/role name)
   - `permission` (enum set or bitmask)
   - `is_inherited` (boolean) — derived flag for debugging/analytics
   - `created_at`

### ERD Sketch
```
User --< permission_grant >-- ResourceNode --< ResourceEdge >-- ResourceNode
                                ^
                                |
                              owner
```

## Permission Resolution
Use a recursive CTE to walk ancestors and accumulate permissions:
```sql
WITH RECURSIVE ancestor AS (
    SELECT rn.id, rn.parent_id
    FROM resource_node rn
    WHERE rn.id = :resourceId
  UNION ALL
    SELECT p.id, p.parent_id
    FROM resource_edge e
    JOIN resource_node p ON p.id = e.parent_id
    JOIN ancestor a ON a.id = e.child_id
)
SELECT DISTINCT pg.permission
FROM ancestor a
JOIN permission_grant pg ON pg.resource_id = a.id
WHERE (pg.subject_type = 'USER' AND pg.subject_id = :userId)
   OR (pg.subject_type = 'ROLE' AND pg.subject_id = ANY(:roles));
```
- Grant resolution merges permissions from closest ancestor to root; explicit denies can be modeled by adding a `EFFECT` column (`ALLOW`/`DENY`) and evaluating order.

## Inheritance Rules
- A child node inherits all **allow** permissions from its nearest ancestor unless a deny exists at the child.
- Owners receive `ADMIN` (or configurable default) on the node they create.
- System roles (e.g., `ADMIN`, `EVENT_MANAGER`) can receive root-level grants on the Event node to cascade.

## Lifecycle Hooks
- **Create** (event/post/comment/like):
  - Insert into `resource_node` with owner and type.
  - Insert `resource_edge` linking to parent.
  - Insert default `permission_grant` for owner and any system defaults.
- **Soft-delete**:
  - Update `resource_node.status` to `SOFT_DELETED`.
  - Optionally flag related grants as disabled (`permission_grant.active = false`).
- **Hard-delete** (if ever used):
  - Delete edges → grants → node, in that order.

## JPA Considerations
- Map `ResourceNode` and `ResourceEdge` as entities with `@OneToMany`/`@ManyToOne` to navigate the graph.
- Map `PermissionGrant` with composite natural key `(resource, subjectType, subjectId, permission)` to avoid duplicates.
- Use a Spring Data repository method with `@Query` for the recursive CTE; ensure `nativeQuery = true` for PostgreSQL.
- Encapsulate resolution in a service method `PermissionService.hasPermission(userId, roles, resourceId, permission)` that runs the CTE and caches results when possible.

## Default Behavior Examples
- **Event creation**: creator gets `ADMIN`; `EVENT_MANAGER` role gets `MODERATE` on the event (cascades to posts/comments/likes).
- **Post creation**: post owner gains `MODERATE` on the post; inherits `VIEW/COMMENT` from the event automatically.
- **Comment/Like**: owner gets `VIEW`/`COMMENT` as appropriate; inherits higher privileges from ancestor posts/events.

## Notes
- Keep the recursive CTE small by limiting to a single ancestor chain (resource graphs remain tree-like for this use case).
- For analytics/debugging, store the computed ancestor depth to explain why a permission was granted.
