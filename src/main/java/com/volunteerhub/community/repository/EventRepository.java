package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.db_enum.EventState;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("UPDATE Event e SET e.eventState = :eventState WHERE e.eventId = :eventId")
    int updateEventStatus(@Param("eventId") Long eventId, @Param("eventState") EventState eventState);

    @Query("""
            SELECT e FROM Event e
            WHERE (:since IS NULL OR e.createdAt >= :since)
            ORDER BY e.createdAt DESC
            """)
    List<Event> findRecentEvents(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
            SELECT e FROM Event e
            LEFT JOIN Like l ON l.targetId = e.eventId AND l.tableType = com.volunteerhub.community.model.db_enum.TableType.EVENT
            WHERE (:since IS NULL OR e.createdAt >= :since)
            GROUP BY e
            ORDER BY COUNT(l) DESC, e.createdAt DESC
            """)
    List<Event> findTrendingEvents(@Param("since") LocalDateTime since, Pageable pageable);
}
