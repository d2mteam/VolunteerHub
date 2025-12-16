# Counter cache plan

**Key format**
- Post like count: `post:{postId}:likeCount`
- Comment like count: `comment:{commentId}:likeCount`
- Event like count: `event:{eventId}:likeCount`
- Event member count: `event:{eventId}:memberCount`
- Event post count: `event:{eventId}:postCount`

**TTL & resilience**
- Default TTL: 180s for all counters (configurable via `counter-cache.*` in `application.yml`).
- Redis timeout: 40ms with a simple circuit breaker (opens after 3 failures for 30s) and single-flight coalescing (120ms window).

**DataLoader usage**
- Per-request loaders: `likeCountLoader`, `memberCountLoader`, `postCountLoader`.
- Each loader batches Redis `MGET` calls; cache misses fall back to grouped DB queries and backfill Redis when healthy.

**Invalidation hooks (best-effort)**
- Like/unlike → delete `likeCount` key for the target.
- Event registration changes → delete `event:{id}:memberCount`.
- Post create/delete → delete `event:{id}:postCount`.

**Ordering and defaults**
- Results always align with the incoming ID order.
- Missing records return `0` (never `null`).
