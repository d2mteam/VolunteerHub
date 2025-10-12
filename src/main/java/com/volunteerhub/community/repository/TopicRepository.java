package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Page<Topic> findByCreatedBy_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
