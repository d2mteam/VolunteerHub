package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Page<Registration> findByEvent_EventId(Long eventId, Pageable pageable);
    Page<Registration> findByCreatedBy_UserId(UUID userId, Pageable pageable);
    boolean existsByEvent_EventIdAndCreatedBy_UserId(Long eventId, UUID userId);
}
