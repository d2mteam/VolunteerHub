package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.RoleInEvent;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface RoleInEventRepository extends JpaRepository<RoleInEvent, Long> {
    boolean existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusIn(
            UUID userProfile_userId, Long event_eventId, Collection<ParticipationStatus> participationStatus);
}
