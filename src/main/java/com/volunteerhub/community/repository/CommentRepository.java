package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.table.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
