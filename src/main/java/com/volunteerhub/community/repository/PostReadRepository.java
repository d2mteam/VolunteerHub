package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.read.PostRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReadRepository extends JpaRepository<PostRead, Long> {
    Page<PostRead> findByEventId(Long eventId, Pageable pageable);
}
