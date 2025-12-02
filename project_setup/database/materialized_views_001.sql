-- ============================================================
-- FILE: materialized_views_001.sql
-- PURPOSE: Materialized Views cho layer đọc (CQRS Read Model)
-- ============================================================


-- ============================================================
-- 1. MATERIALIZED VIEW: event_detail_mv
-- Mục đích: phục vụ query getEvent(eventId)
-- ============================================================
DROP MATERIALIZED VIEW IF EXISTS event_detail_mv;
DROP MATERIALIZED VIEW IF EXISTS user_profile_detail_mv;
DROP MATERIALIZED VIEW IF EXISTS comment_detail_mv;
DROP MATERIALIZED VIEW IF EXISTS post_detail_mv;

DROP FUNCTION IF EXISTS refresh_event_detail();
DROP FUNCTION IF EXISTS refresh_post_detail();
DROP FUNCTION IF EXISTS refresh_comment_detail();
DROP FUNCTION IF EXISTS refresh_user_profile_detail();

CREATE MATERIALIZED VIEW event_detail_mv AS
SELECT e.event_id,
       e.event_name,
       e.event_description,
       e.event_location,
       e.created_at,
       e.updated_at,
--        e.created_by                      AS creator_id, -- GraphQL: createdBy
--        e.event_metadata ,

       -- Denormalized summary: username, avatar của người tạo
--        (SELECT u.username
--         FROM user_profiles u
--         WHERE u.user_id = e.created_by)  AS creator_username,

--        (SELECT u.avatar_url
--         FROM user_profiles u
--         WHERE u.user_id = e.created_by)  AS creator_avatar,

--        (SELECT u.full_name
--         FROM user_profiles u
--         WHERE u.user_id = e.created_by)  AS creator_full_name,

       -- Tổng số thành viên tham gia event
       (SELECT COUNT(DISTINCT rie.user_profile_id)
        FROM role_in_event rie
        WHERE rie.event_id = e.event_id) AS member_count,

       -- Tổng số bài post thuộc event
       (SELECT COUNT(*)
        FROM posts p
        WHERE p.event_id = e.event_id)   AS post_count,

       -- Tổng số lượt like của event
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'EVENT'
          AND l.target_id = e.event_id)  AS like_count

FROM events e
WHERE e.event_state IN ('ACCEPTED', 'PENDING');


-- ============================================================
-- 2. MATERIALIZED VIEW: post_detail_mv
-- Mục đích: phục vụ query getPost(postId)
-- ============================================================
CREATE MATERIALIZED VIEW post_detail_mv AS
SELECT p.post_id,
       p.event_id,
       p.content,
       p.created_at,
       p.updated_at,
       p.created_by                     AS creator_id,

--        -- Denormalized thông tin người tạo
--        (SELECT u.username
--         FROM user_profiles u
--         WHERE u.user_id = p.created_by) AS creator_username,
--        (SELECT u.avatar_url
--         FROM user_profiles u
--         WHERE u.user_id = p.created_by) AS creator_avatar,
--        (SELECT u.full_name
--         FROM user_profiles u
--         WHERE u.user_id = p.created_by) AS creator_full_name,
       -- Tổng số comment trong post
       (SELECT COUNT(*)
        FROM comments c
        WHERE c.post_id = p.post_id)    AS comment_count,

       -- Tổng số like cho post
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'POST'
          AND l.target_id = p.post_id)  AS like_count

FROM posts p;


-- ============================================================
-- 3. MATERIALIZED VIEW: comment_detail_mv
-- Mục đích: phục vụ query comment list của post
-- ============================================================
CREATE MATERIALIZED VIEW comment_detail_mv AS
SELECT c.comment_id,
       c.post_id,
       c.content,
       c.created_at,
       c.updated_at,
       c.created_by                       AS creator_id,

       -- Denormalized thông tin người tạo
--        (SELECT u.username
--         FROM user_profiles u
--         WHERE u.user_id = c.created_by)   AS creator_username,
-- --        (SELECT u.avatar_url
--         FROM user_profiles u
--         WHERE u.user_id = c.created_by)   AS creator_avatar,
--        (SELECT u.full_name
--         FROM user_profiles u
--         WHERE u.user_id = c.created_by)   AS creator_full_name,
       -- Tổng số like của comment
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'COMMENT'
          AND l.target_id = c.comment_id) AS like_count

FROM comments c;


-- ============================================================
-- 4. MATERIALIZED VIEW: user_profile_detail_mv
-- Mục đích: phục vụ query getUserProfile(userId)
-- ============================================================
CREATE MATERIALIZED VIEW user_profile_detail_mv AS
SELECT u.user_id,
       u.username,
       u.full_name,
       u.created_at,
       u.updated_at,
--        u.avatar_url,
       u.email,
       u.status,

       -- Tổng số post user tạo
       (SELECT COUNT(*)
        FROM posts p
        WHERE p.created_by = u.user_id)        AS post_count,

       -- Tổng số comment user tạo
       (SELECT COUNT(*)
        FROM comments c
        WHERE c.created_by = u.user_id)        AS comment_count,

       -- Tổng số event user tham gia
       (SELECT COUNT(DISTINCT rie.event_id)
        FROM role_in_event rie
        WHERE rie.user_profile_id = u.user_id) AS event_count,

       -- Tổng số lượt like user đã thực hiện
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.created_by = u.user_id)        AS like_count

FROM user_profiles u;


-- ============================================================
-- 5. REFRESH SECTION
-- Mục đích: cập nhật dữ liệu sau khi insert/update/delete nguồn
-- ============================================================

CREATE OR REPLACE FUNCTION refresh_event_detail() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW event_detail_mv;
END
$$;

CREATE OR REPLACE FUNCTION refresh_post_detail() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW post_detail_mv;
END
$$;

CREATE OR REPLACE FUNCTION refresh_comment_detail() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW comment_detail_mv;
END
$$;

CREATE OR REPLACE FUNCTION refresh_user_profile_detail() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW user_profile_detail_mv;
END
$$;

-- ============================================================
-- END OF FILE
-- ============================================================
