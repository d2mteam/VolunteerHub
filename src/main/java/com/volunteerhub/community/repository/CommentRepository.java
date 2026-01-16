package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost_PostId(Long postId, Pageable pageable);

    @Query("SELECT c.createdBy.userId FROM Comment c WHERE c.commentId = :commentId")
    Optional<java.util.UUID> findCreatedByUserId(@Param("commentId") Long commentId);

    @Query("SELECT c.post.postId FROM Comment c WHERE c.commentId = :commentId")
    Optional<Long> findPostIdByCommentId(@Param("commentId") Long commentId);
}
