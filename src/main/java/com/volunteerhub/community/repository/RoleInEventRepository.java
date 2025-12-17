package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.RoleInEvent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleInEventRepository extends JpaRepository<RoleInEvent, Long> {
    Optional<RoleInEvent> findByUserProfile_UserIdAndEvent_EventId(UUID userId, Long eventId);

    @EntityGraph(value = "RoleInEvent.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<RoleInEvent> findByUserProfile_UserId(UUID userId, Pageable pageable);

    @EntityGraph(value = "RoleInEvent.RoleInEvent.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<RoleInEvent> findByEvent_EventId(Long eventId, Pageable pageable);
}
