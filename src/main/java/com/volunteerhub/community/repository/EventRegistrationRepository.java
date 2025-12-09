package com.volunteerhub.community.repository;

import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.model.EventRegistration;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndStatus(UUID userId, RegistrationStatus status);

    @Query("""
            SELECT new com.volunteerhub.community.dto.CountById(er.event.eventId, COUNT(er))
            FROM EventRegistration er
            WHERE er.event.eventId IN :eventIds
            GROUP BY er.event.eventId
            """)
    List<CountById> countByEventIds(List<Long> eventIds);
}
