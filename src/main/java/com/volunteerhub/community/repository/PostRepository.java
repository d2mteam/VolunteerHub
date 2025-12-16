package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByEvent_EventId(Long eventId, Pageable pageable);
}
