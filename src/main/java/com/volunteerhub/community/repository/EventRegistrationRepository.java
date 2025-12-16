package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.EventRegistration;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByUserProfile_UserIdAndEvent_EventIdAndStatus(UUID userId, Long eventId, RegistrationStatus registrationStatus);
}
