/* =======================================================
   READ MODEL VIEWS FOR GRAPHQL (CQRS READ SIDE)
   Author: admin
   Purpose: Denormalized read models for GraphQL queries
   ======================================================= */

/* ======================
   EVENT DETAIL (parent)
   ====================== */
CREATE MATERIALIZED VIEW mv_event_detail AS
SELECT e.event_id,
       e.event_name,
       e.event_description,
       e.event_location,
       e.created_at,
       e.updated_at,
       e.created_by                     AS creator_id,
       u.username                       AS creator_username,
       u.avatar_url                     AS creator_avatar,
       u.role                           AS creator_role,
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'EVENT'
          AND l.target_id = e.event_id) AS like_count,
       (SELECT COUNT(*)
        FROM posts p
        WHERE p.event_id = e.event_id)  AS post_count,
       (SELECT COUNT(*)
        FROM role_in_event re
        WHERE re.event_id = e.event_id) AS member_count
FROM events e
         JOIN user_profiles u ON u.user_id = e.created_by;

ALTER MATERIALIZED VIEW mv_event_detail OWNER TO admin;
CREATE UNIQUE INDEX idx_mv_event_detail_event_id ON mv_event_detail (event_id);
CREATE INDEX idx_mv_event_detail_created_at ON mv_event_detail (created_at DESC);

/* ======================
   POST DETAIL (parent)
   ====================== */
CREATE MATERIALIZED VIEW mv_post_detail AS
SELECT p.post_id,
       p.event_id,
       p.content,
       p.created_at,
       p.updated_at,
       p.created_by                    AS creator_id,
       u.username                      AS creator_username,
       u.avatar_url                    AS creator_avatar,
       u.role                          AS creator_role,
       (SELECT COUNT(*)
        FROM likes l
        WHERE l.target_type = 'POST'
          AND l.target_id = p.post_id) AS like_count,
       (SELECT COUNT(*)
        FROM comments c
        WHERE c.post_id = p.post_id)   AS comment_count
FROM posts p
         JOIN user_profiles u ON u.user_id = p.created_by;

ALTER MATERIALIZED VIEW mv_post_detail OWNER TO admin;
CREATE UNIQUE INDEX idx_mv_post_detail_post_id ON mv_post_detail (post_id);
CREATE INDEX idx_mv_post_detail_event_id_created_at ON mv_post_detail (event_id ASC, created_at DESC);
CREATE INDEX idx_mv_post_detail_creator_id_created_at ON mv_post_detail (creator_id ASC, created_at DESC);

/* ======================
   USER PROFILE SUMMARY (parent)
   ====================== */
CREATE MATERIALIZED VIEW mv_user_profile_summary AS
SELECT u.user_id,
       u.username,
       u.avatar_url,
       u.email,
       u.role,
       u.status,
       u.created_at,
       (SELECT COUNT(*)
        FROM posts p
        WHERE p.created_by = u.user_id)       AS post_count,
       (SELECT COUNT(*)
        FROM comments c
        WHERE c.created_by = u.user_id)       AS comment_count,
       (SELECT COUNT(*)
        FROM role_in_event re
        WHERE re.user_profile_id = u.user_id) AS event_count
FROM user_profiles u;

ALTER MATERIALIZED VIEW mv_user_profile_summary OWNER TO admin;
CREATE UNIQUE INDEX idx_mv_user_profile_summary_user_id ON mv_user_profile_summary (user_id);
CREATE INDEX idx_mv_user_profile_summary_created_at ON mv_user_profile_summary (created_at DESC);

/* ======================
   EVENT → POSTS (child)
   ====================== */
CREATE MATERIALIZED VIEW mv_event_posts AS
SELECT p.post_id,
       p.event_id,
       p.content,
       p.created_at,
       p.updated_at,
       p.created_by                  AS creator_id,
       u.username                    AS creator_username,
       u.avatar_url                  AS creator_avatar,
       u.role                        AS creator_role,
       COALESCE(pc.comment_count, 0) AS comment_count,
       COALESCE(pl.like_count, 0)    AS like_count
FROM posts p
         JOIN user_profiles u ON u.user_id = p.created_by
         LEFT JOIN (SELECT c.post_id, COUNT(*) AS comment_count
                    FROM comments c
                    GROUP BY c.post_id) pc ON pc.post_id = p.post_id
         LEFT JOIN (SELECT l.target_id AS post_id, COUNT(*) AS like_count
                    FROM likes l
                    WHERE l.target_type = 'POST'
                    GROUP BY l.target_id) pl ON pl.post_id = p.post_id
ORDER BY p.created_at DESC;

ALTER MATERIALIZED VIEW mv_event_posts OWNER TO admin;
CREATE UNIQUE INDEX idx_mv_event_posts_post_id ON mv_event_posts (post_id);
CREATE INDEX idx_mv_event_posts_event_id_created_at ON mv_event_posts (event_id ASC, created_at DESC);

/* ======================
   POST → COMMENTS (child)
   ====================== */
CREATE MATERIALIZED VIEW mv_post_comments AS
SELECT c.comment_id,
       c.post_id,
       c.content,
       c.created_at,
       c.updated_at,
       c.created_by              AS creator_id,
       u.username                AS creator_username,
       u.avatar_url              AS creator_avatar,
       u.role                    AS creator_role,
       COALESCE(l.like_count, 0) AS like_count
FROM comments c
         JOIN user_profiles u ON u.user_id = c.created_by
         LEFT JOIN (SELECT l.target_id AS comment_id, COUNT(*) AS like_count
                    FROM likes l
                    WHERE l.target_type = 'COMMENT'
                    GROUP BY l.target_id) l ON l.comment_id = c.comment_id
ORDER BY c.created_at DESC;

ALTER MATERIALIZED VIEW mv_post_comments OWNER TO admin;
CREATE UNIQUE INDEX idx_mv_post_comments_comment_id ON mv_post_comments (comment_id);
CREATE INDEX idx_mv_post_comments_post_id_created_at ON mv_post_comments (post_id ASC, created_at DESC);

/* ======================
   USER → EVENTS (child)
   ====================== */
CREATE MATERIALIZED VIEW mv_user_events AS
SELECT re.user_profile_id           AS user_id,
       re.event_id,
       e.event_name,
       e.event_description,
       e.event_location,
       e.created_at,
       COALESCE(pc.post_count, 0)   AS post_count,
       COALESCE(mc.member_count, 0) AS member_count
FROM role_in_event re
         JOIN events e ON e.event_id = re.event_id
         LEFT JOIN (SELECT p.event_id, COUNT(*) AS post_count
                    FROM posts p
                    GROUP BY p.event_id) pc ON pc.event_id = e.event_id
         LEFT JOIN (SELECT r.event_id, COUNT(*) AS member_count
                    FROM role_in_event r
                    GROUP BY r.event_id) mc ON mc.event_id = e.event_id
ORDER BY e.created_at DESC;

ALTER MATERIALIZED VIEW mv_user_events OWNER TO admin;
CREATE INDEX idx_mv_user_events_user_id ON mv_user_events (user_id);

/* ======================
   REFRESH FUNCTIONS
   ====================== */
CREATE OR REPLACE FUNCTION refresh_mv_event() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_event_detail;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_event_posts;
END
$$;

CREATE OR REPLACE FUNCTION refresh_mv_post() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_post_detail;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_post_comments;
END
$$;

CREATE OR REPLACE FUNCTION refresh_mv_user() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_profile_summary;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_user_events;
END
$$;

/* ======================
   OPTIONAL: GLOBAL REFRESH WRAPPER
   ====================== */
CREATE OR REPLACE FUNCTION refresh_all_read_models() RETURNS void
    LANGUAGE plpgsql AS
$$
BEGIN
    PERFORM refresh_mv_event();
    PERFORM refresh_mv_post();
    PERFORM refresh_mv_user();
END
$$;
