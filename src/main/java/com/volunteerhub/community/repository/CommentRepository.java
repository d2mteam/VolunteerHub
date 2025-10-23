package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
