package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.read.EventActivitySummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventActivitySummaryRepository extends JpaRepository<EventActivitySummary, Long> {
    List<EventActivitySummary> findByOrderByCreatedAtDesc(Pageable pageable);

    List<EventActivitySummary> findByLatestPostAtAfterOrderByLatestPostAtDesc(LocalDateTime since, Pageable pageable);

    @Query("""
            SELECT e
            FROM EventActivitySummary e
            WHERE e.latestInteractionAt >= :since
            ORDER BY (e.newMemberCount + e.newCommentCount + e.newLikeCount + e.newPostCount) DESC
            """)
    List<EventActivitySummary> findTrendingSince(@Param("since") LocalDateTime since, Pageable pageable);
}
