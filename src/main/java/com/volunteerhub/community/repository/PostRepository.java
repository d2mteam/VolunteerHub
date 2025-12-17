package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.repository.projection.PostDashboardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByEvent_EventId(Long eventId, Pageable pageable);

    @Query(value = """
            SELECT p.post_id    AS postId,
                   p.event_id   AS eventId,
                   p.created_at AS createdAt,
                   COALESCE(c.comment_count, 0) AS commentCount,
                   COALESCE(l.like_count, 0)    AS likeCount
            FROM posts p
                     LEFT JOIN (
                SELECT post_id, COUNT(*) AS comment_count
                FROM comments
                WHERE (:since IS NULL OR created_at >= :since)
                GROUP BY post_id
            ) c ON c.post_id = p.post_id
                     LEFT JOIN (
                SELECT target_id, COUNT(*) AS like_count
                FROM likes
                WHERE target_type = 'POST' AND (:since IS NULL OR created_at >= :since)
                GROUP BY target_id
            ) l ON l.target_id = p.post_id
            WHERE (:recentOnly = FALSE OR (:since IS NOT NULL AND p.created_at >= :since))
              AND (:hasEventFilter = FALSE OR p.event_id IN (:eventIds))
            ORDER BY CASE
                         WHEN :trending = TRUE THEN (COALESCE(c.comment_count, 0) + COALESCE(l.like_count, 0))
                         ELSE 0 END DESC,
                     p.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<PostDashboardProjection> fetchDashboardPosts(
            @Param("recentOnly") boolean recentOnly,
            @Param("trending") boolean trending,
            @Param("since") LocalDateTime since,
            @Param("limit") int limit,
            @Param("hasEventFilter") boolean hasEventFilter,
            @Param("eventIds") List<Long> eventIds
    );
}
