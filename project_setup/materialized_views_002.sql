-- ============================================================
-- FILE: materialized_views_002.sql
-- PURPOSE: CQRS Read Model (Incremental Views with pg_ivm)
-- STRUCTURE:
--   1. Summary Count Views (Tầng 1)
--   2. Detail Views (Tầng 2)
--   3. Indexes (for refresh concurrently)
-- ============================================================

-- Requires:
--   CREATE EXTENSION pg_ivm;

-- ============================================================
-- CLEANUP
-- ============================================================
DROP MATERIALIZED VIEW IF EXISTS post_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS member_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS event_like_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS comment_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS post_like_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS comment_like_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS user_post_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS user_comment_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS user_event_count_ivm;
DROP MATERIALIZED VIEW IF EXISTS user_like_count_ivm;

DROP MATERIALIZED VIEW IF EXISTS event_detail_ivm;
DROP MATERIALIZED VIEW IF EXISTS post_detail_ivm;
DROP MATERIALIZED VIEW IF EXISTS comment_detail_ivm;
DROP MATERIALIZED VIEW IF EXISTS user_profile_detail_ivm;

-- ============================================================
-- 1️⃣ SUMMARY COUNT VIEWS (TẦNG 1)
-- ============================================================

-- Tổng post mỗi event
CREATE INCREMENTAL MATERIALIZED VIEW post_count_ivm AS
SELECT event_id, COUNT(*) AS post_count
FROM posts
GROUP BY event_id;

-- Tổng thành viên mỗi event
CREATE INCREMENTAL MATERIALIZED VIEW member_count_ivm AS
SELECT event_id, COUNT(DISTINCT user_profile_id) AS member_count
FROM role_in_event
GROUP BY event_id;

-- Tổng like cho event
CREATE INCREMENTAL MATERIALIZED VIEW event_like_count_ivm AS
SELECT target_id AS event_id, COUNT(*) AS like_count
FROM likes
WHERE target_type = 'EVENT'
GROUP BY target_id;

-- Tổng comment mỗi post
CREATE INCREMENTAL MATERIALIZED VIEW comment_count_ivm AS
SELECT post_id, COUNT(*) AS comment_count
FROM comments
GROUP BY post_id;

-- Tổng like cho post
CREATE INCREMENTAL MATERIALIZED VIEW post_like_count_ivm AS
SELECT target_id AS post_id, COUNT(*) AS like_count
FROM likes
WHERE target_type = 'POST'
GROUP BY target_id;

-- Tổng like cho comment
CREATE INCREMENTAL MATERIALIZED VIEW comment_like_count_ivm AS
SELECT target_id AS comment_id, COUNT(*) AS like_count
FROM likes
WHERE target_type = 'COMMENT'
GROUP BY target_id;

-- Tổng post / comment / like / event mỗi user
CREATE INCREMENTAL MATERIALIZED VIEW user_post_count_ivm AS
SELECT created_by AS user_id, COUNT(*) AS post_count
FROM posts
GROUP BY created_by;

CREATE INCREMENTAL MATERIALIZED VIEW user_comment_count_ivm AS
SELECT created_by AS user_id, COUNT(*) AS comment_count
FROM comments
GROUP BY created_by;

CREATE INCREMENTAL MATERIALIZED VIEW user_like_count_ivm AS
SELECT created_by AS user_id, COUNT(*) AS like_count
FROM likes
GROUP BY created_by;

CREATE INCREMENTAL MATERIALIZED VIEW user_event_count_ivm AS
SELECT user_profile_id AS user_id, COUNT(DISTINCT event_id) AS event_count
FROM role_in_event
GROUP BY user_profile_id;

-- ============================================================
-- 2️⃣ DETAIL VIEWS (TẦNG 2)
-- ============================================================

-- EVENT DETAIL VIEW
CREATE INCREMENTAL MATERIALIZED VIEW event_detail_ivm AS
SELECT
    e.event_id,
    e.event_name,
    e.event_description,
    e.event_location,
    e.created_at,
    e.updated_at,
    e.created_by AS creator_id,
    u.username AS creator_username,
    u.avatar_url AS creator_avatar,
    u.full_name AS creator_full_name,
    COALESCE(p.post_count, 0)   AS post_count,
    COALESCE(m.member_count, 0) AS member_count,
    COALESCE(l.like_count, 0)   AS like_count
FROM events e
         LEFT JOIN user_profiles u ON u.user_id = e.created_by
         LEFT JOIN post_count_ivm p ON p.event_id = e.event_id
         LEFT JOIN member_count_ivm m ON m.event_id = e.event_id
         LEFT JOIN event_like_count_ivm l ON l.event_id = e.event_id
WHERE e.event_state IN ('Accepted', 'Pending');

-- POST DETAIL VIEW
CREATE INCREMENTAL MATERIALIZED VIEW post_detail_ivm AS
SELECT
    p.post_id,
    p.event_id,
    p.content,
    p.created_at,
    p.updated_at,
    p.created_by AS creator_id,
    u.username AS creator_username,
    u.avatar_url AS creator_avatar,
    u.full_name AS creator_full_name,
    COALESCE(c.comment_count, 0) AS comment_count,
    COALESCE(l.like_count, 0)    AS like_count
FROM posts p
         LEFT JOIN user_profiles u ON u.user_id = p.created_by
         LEFT JOIN comment_count_ivm c ON c.post_id = p.post_id
         LEFT JOIN post_like_count_ivm l ON l.post_id = p.post_id;

-- COMMENT DETAIL VIEW
CREATE INCREMENTAL MATERIALIZED VIEW comment_detail_ivm AS
SELECT
    c.comment_id,
    c.post_id,
    c.content,
    c.created_at,
    c.updated_at,
    c.created_by AS creator_id,
    u.username AS creator_username,
    u.avatar_url AS creator_avatar,
    u.full_name AS creator_full_name,
    COALESCE(l.like_count, 0) AS like_count
FROM comments c
         LEFT JOIN user_profiles u ON u.user_id = c.created_by
         LEFT JOIN comment_like_count_ivm l ON l.comment_id = c.comment_id;

-- USER PROFILE DETAIL VIEW
CREATE INCREMENTAL MATERIALIZED VIEW user_profile_detail_ivm AS
SELECT
    u.user_id,
    u.username,
    u.full_name,
    u.created_at,
    u.avatar_url,
    u.email,
    u.status,
    COALESCE(p.post_count, 0) AS post_count,
    COALESCE(c.comment_count, 0) AS comment_count,
    COALESCE(e.event_count, 0) AS event_count,
    COALESCE(l.like_count, 0) AS like_count
FROM user_profiles u
         LEFT JOIN user_post_count_ivm p ON p.user_id = u.user_id
         LEFT JOIN user_comment_count_ivm c ON c.user_id = u.user_id
         LEFT JOIN user_event_count_ivm e ON e.user_id = u.user_id
         LEFT JOIN user_like_count_ivm l ON l.user_id = u.user_id;

-- ============================================================
-- 3️⃣ INDEXES (optional, for concurrent refresh)
-- ============================================================

CREATE UNIQUE INDEX IF NOT EXISTS idx_post_count_ivm ON post_count_ivm(event_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_member_count_ivm ON member_count_ivm(event_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_event_like_count_ivm ON event_like_count_ivm(event_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_comment_count_ivm ON comment_count_ivm(post_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_post_like_count_ivm ON post_like_count_ivm(post_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_comment_like_count_ivm ON comment_like_count_ivm(comment_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_post_count_ivm ON user_post_count_ivm(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_comment_count_ivm ON user_comment_count_ivm(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_event_count_ivm ON user_event_count_ivm(user_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_like_count_ivm ON user_like_count_ivm(user_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_event_detail_ivm ON event_detail_ivm(event_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_post_detail_ivm ON post_detail_ivm(post_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_comment_detail_ivm ON comment_detail_ivm(comment_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_profile_detail_ivm ON user_profile_detail_ivm(user_id);

-- ============================================================
-- END OF FILE
-- ============================================================
