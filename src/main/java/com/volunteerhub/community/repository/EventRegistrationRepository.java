package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.EventRegistration;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByUserIdAndEventIdAndStatus(UUID userId,
                                                                Long eventId,
                                                                RegistrationStatus registrationStatus);

    @EntityGraph(value = "EventRegistration.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<EventRegistration> findByUserId(UUID userId, Pageable pageable);

    @EntityGraph(value = "EventRegistration.full", type = EntityGraph.EntityGraphType.FETCH)
    Page<EventRegistration> findByEventId(Long eventId, Pageable pageable);
}