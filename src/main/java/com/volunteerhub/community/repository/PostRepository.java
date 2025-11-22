package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.table.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
