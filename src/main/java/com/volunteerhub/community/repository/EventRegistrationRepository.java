package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.EventRegistration;
import com.volunteerhub.community.entity.db_enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);
}
