package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
