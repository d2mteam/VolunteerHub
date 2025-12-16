package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByEvent_EventId(Long eventId, Pageable pageable);

    @Query("SELECT p.event.eventId AS eventId, COUNT(p) AS count " +
            "FROM Post p " +
            "WHERE p.event.eventId IN :eventIds " +
            "GROUP BY p.event.eventId")
    List<PostCountProjection> countByEventIds(@Param("eventIds") Iterable<Long> eventIds);
}

public interface PostCountProjection {
    Long getEventId();

    Long getCount();
}
