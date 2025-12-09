package com.volunteerhub.community.repository;

import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost_PostId(Long postId, Pageable pageable);

    @Query("""
            SELECT new com.volunteerhub.community.dto.CountById(c.post.postId, COUNT(c))
            FROM Comment c
            WHERE c.post.postId IN :postIds
            GROUP BY c.post.postId
            """)
    List<CountById> countByPostIds(List<Long> postIds);
}
