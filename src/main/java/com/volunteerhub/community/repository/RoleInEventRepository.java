package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.entity.RoleInEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RoleInEventRepository extends JpaRepository<RoleInEvent, Long> {
    Optional<RoleInEvent> findByUserProfile_UserIdAndEvent_EventId(UUID userId, Long eventId);

    Page<RoleInEvent> findByUserProfile_UserId(UUID userId, Pageable pageable);

    long countByEvent_EventIdAndParticipationStatusIn(@Param("eventId") Long eventId,
                                                      @Param("statuses") Iterable<ParticipationStatus> statuses);
}
