package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.table.EventRegistration;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndStatus(UUID userId, RegistrationStatus status);
}
