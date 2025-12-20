package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.entity.RoleInEvent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface RoleInEventRepository extends JpaRepository<RoleInEvent, Long> {

    Optional<RoleInEvent> findByUserProfile_UserIdAndEvent_EventId(UUID userId, Long eventId);

    @EntityGraph(value = "RoleInEvent.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<RoleInEvent> findByUserProfile_UserId(UUID userId, Pageable pageable);

    @EntityGraph(value = "RoleInEvent.RoleInEvent.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<RoleInEvent> findByEvent_EventId(Long eventId, Pageable pageable);


    @Modifying
    @Transactional
    @Query("""
                UPDATE RoleInEvent rie
                SET
                    rie.participationStatus = com.volunteerhub.community.model.db_enum.ParticipationStatus.COMPLETED,
                    rie.updatedAt = CURRENT_TIMESTAMP
                WHERE
                    rie.event.eventId = :eventId
                AND
                    rie.userProfile.userId = :userId
            """)
    int markParticipationCompleted(@Param("eventId") Long eventId, @Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("""
                UPDATE RoleInEvent rie
                SET
                    rie.participationStatus = :participationStatus,
                    rie.updatedAt = CURRENT_TIMESTAMP
                WHERE
                    rie.event.eventId = :eventId
                AND
                    rie.userProfile.userId = :userId
            """)
    int changeParticipationStatus(@Param("eventId") Long eventId, @Param("userId") UUID userId, @Param("participationStatus") ParticipationStatus participationStatus);

    @Query("""
                SELECT COUNT(rie)
                FROM RoleInEvent rie
                WHERE rie.event.eventId = :eventId AND rie.participationStatus IN (
                          com.volunteerhub.community.model.db_enum.ParticipationStatus.APPROVED,
                          com.volunteerhub.community.model.db_enum.ParticipationStatus.COMPLETED)
            """)
    long countByEvent(@Param("eventId") Long eventId);

    boolean existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusNotIn(
            UUID userId,
            Long eventId,
            Collection<ParticipationStatus> statuses
    );

    @Query("SELECT rie.participationStatus FROM RoleInEvent rie WHERE rie.userProfile.userId = :userId AND rie.event.eventId = :eventId")
    Optional<ParticipationStatus> findParticipationStatus(@Param("userId") UUID userId, @Param("eventId") Long eventId);
}
