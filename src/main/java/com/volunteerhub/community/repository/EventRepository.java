package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.db_enum.EventState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.volunteerhub.community.repository.projection.EventDashboardProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("UPDATE Event e SET e.eventState = :eventState WHERE e.eventId = :eventId")
    int updateEventStatus(@Param("eventId") Long eventId, @Param("eventState") EventState eventState);

    @Query(value = """
            SELECT e.event_id   AS eventId,
                   e.event_name AS eventName,
                   e.created_at AS createdAt,
                   e.created_by AS creatorId,
                   COALESCE(mem.member_count, 0) AS memberCount,
                   COALESCE(po.post_count, 0)   AS postCount,
                   COALESCE(li.like_count, 0)   AS likeCount
            FROM events e
                     LEFT JOIN (
                SELECT event_id, COUNT(*) AS member_count
                FROM role_in_event
                WHERE (:since IS NULL OR created_at >= :since)
                GROUP BY event_id
            ) mem ON mem.event_id = e.event_id
                     LEFT JOIN (
                SELECT event_id, COUNT(*) AS post_count
                FROM posts
                WHERE (:since IS NULL OR created_at >= :since)
                GROUP BY event_id
            ) po ON po.event_id = e.event_id
                     LEFT JOIN (
                SELECT target_id, COUNT(*) AS like_count
                FROM likes
                WHERE target_type = 'EVENT' AND (:since IS NULL OR created_at >= :since)
                GROUP BY target_id
            ) li ON li.target_id = e.event_id
            WHERE (:recentOnly = FALSE OR (:since IS NOT NULL AND e.created_at >= :since))
            ORDER BY CASE
                         WHEN :trending = TRUE THEN (COALESCE(mem.member_count, 0) + COALESCE(po.post_count, 0) +
                                                     COALESCE(li.like_count, 0))
                         ELSE 0 END DESC,
                     e.created_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<EventDashboardProjection> fetchDashboardEvents(
            @Param("recentOnly") boolean recentOnly,
            @Param("trending") boolean trending,
            @Param("since") LocalDateTime since,
            @Param("limit") int limit
    );
}
