package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.EventRegistration;
import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndEventIdAndStatus(
            UUID userId, Long eventId, RegistrationStatus status);

    Optional<EventRegistration> findByUserIdAndStatus(UUID userId, RegistrationStatus status);

    @Query("SELECT er.eventId AS eventId, COUNT(er) AS count " +
            "FROM EventRegistration er " +
            "WHERE er.eventId IN :eventIds AND er.status = :status " +
            "GROUP BY er.eventId")
    List<EventRegistrationCountProjection> countByEventIdsAndStatus(@Param("eventIds") Collection<Long> eventIds,
                                                                    @Param("status") RegistrationStatus status);
}

public interface EventRegistrationCountProjection {
    Long getEventId();

    Long getCount();
}
