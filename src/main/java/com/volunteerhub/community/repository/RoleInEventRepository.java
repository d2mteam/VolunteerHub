package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.RoleInEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleInEventRepository extends JpaRepository<RoleInEvent, Long> {
    Optional<RoleInEvent> findByUserProfile_UserIdAndEvent_EventId(UUID userId, Long eventId);
}
