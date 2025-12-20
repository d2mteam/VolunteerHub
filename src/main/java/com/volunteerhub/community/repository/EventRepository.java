package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.db_enum.EventState;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;

import com.volunteerhub.community.repository.view.EventEngagementSummary;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByCreatedBy_UserId(UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Event e SET e.eventState = :eventState WHERE e.eventId = :eventId")
    int updateEventStatus(@Param("eventId") Long eventId, @Param("eventState") EventState eventState);

    Page<Event> findByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            SELECT new com.volunteerhub.community.repository.view.EventEngagementSummary(
                e,
                COUNT(DISTINCT er.registrationId),
                COUNT(DISTINCT c.commentId),
                COUNT(DISTINCT l.likeId),
                MAX(COALESCE(er.createdAt, p.createdAt, c.createdAt, l.createdAt))
            )
            FROM Event e
            LEFT JOIN EventRegistration er ON er.event = e AND er.createdAt >= :since
            LEFT JOIN Post p ON p.event = e AND p.createdAt >= :since
            LEFT JOIN Comment c ON c.post = p AND c.createdAt >= :since
            LEFT JOIN Like l ON l.targetId = e.eventId AND l.tableType = :likeTableType AND l.createdAt >= :since
            GROUP BY e
            HAVING COUNT(er.registrationId) > 0 OR COUNT(c.commentId) > 0 OR COUNT(l.likeId) > 0 OR COUNT(p.postId) > 0
            ORDER BY (COUNT(er.registrationId) + COUNT(c.commentId) + COUNT(l.likeId) + COUNT(p.postId)) DESC
            """)
    List<EventEngagementSummary> findTrendingEventsSince(@Param("since") LocalDateTime since,
                                                         @Param("likeTableType") TableType likeTableType,
                                                         Pageable pageable);


    @Query(
            value = """
                    SELECT e.*
                    FROM events e
                    WHERE
                        (
                            :keyword IS NULL
                            OR lower(e.event_name) LIKE lower(concat(:keyword, '%'))
                        )
                    AND
                        (
                            :location IS NULL
                            OR lower(e.event_location) LIKE lower(concat(:location, '%'))
                        )
                    AND
                        e.created_at >= COALESCE(CAST(:startDateFrom AS timestamp),
                            e.created_at
                        )
                    AND
                        e.created_at <= COALESCE(CAST(:startDateTo AS timestamp),
                            e.created_at
                        )
                    AND
                        e.event_state = COALESCE(CAST(:eventState AS varchar),
                                            e.event_state
                        )
                    AND
                        (:categories IS NULL OR true)
                    ORDER BY e.created_at DESC
                    """,
            nativeQuery = true
    )
    List<Event> searchEvents(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("categories") String[] categories,
            @Param("startDateFrom") LocalDateTime startDateFrom,
            @Param("startDateTo") LocalDateTime startDateTo,
            @Param("eventState") String eventState
    );
}
