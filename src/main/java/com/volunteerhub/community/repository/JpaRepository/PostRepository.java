package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByCreatedBy_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
