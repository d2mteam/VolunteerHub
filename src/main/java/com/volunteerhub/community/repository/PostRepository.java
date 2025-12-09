package com.volunteerhub.community.repository;

import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByEvent_EventId(Long eventId, Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            WHERE (:eventIds IS NULL OR p.eventId IN :eventIds)
            ORDER BY p.createdAt DESC
            """)
    List<Post> findRecentPosts(List<Long> eventIds, Pageable pageable);

    @Query("""
            SELECT p FROM Post p
            WHERE (:eventIds IS NULL OR p.eventId IN :eventIds)
            ORDER BY p.createdAt ASC
            """)
    List<Post> findOldestPosts(List<Long> eventIds, Pageable pageable);

    @Query("""
            SELECT new com.volunteerhub.community.dto.CountById(p.eventId, COUNT(p))
            FROM Post p
            WHERE p.eventId IN :eventIds
            GROUP BY p.eventId
            """)
    List<CountById> countPostsByEventIds(List<Long> eventIds);
}
