package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.db_enum.EventState;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("UPDATE Event e SET e.eventState = :eventState WHERE e.eventId = :eventId")
    int updateEventStatus(@Param("eventId") Long eventId, @Param("eventState") EventState eventState);
}
