package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.repository.view.EventPostActivitySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByEvent_EventId(Long eventId, Pageable pageable);

    @Query("""
            SELECT new com.volunteerhub.community.repository.view.EventPostActivitySummary(
                p.event,
                COUNT(p.postId),
                MAX(p.createdAt)
            )
            FROM Post p
            WHERE p.createdAt >= :since
            GROUP BY p.event
            ORDER BY MAX(p.createdAt) DESC
            """)
    List<EventPostActivitySummary> findEventPostActivitySince(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("""
                SELECT COUNT(p)
                FROM Post p
                WHERE p.event.eventId = :eventId
            """)
    long countByEvent(@Param("eventId") Long eventId);

    @Query("SELECT p.event.eventId FROM Post p WHERE p.postId = :postId")
    Optional<Long> findEventIdByPostId(@Param("postId") Long postId);
}
