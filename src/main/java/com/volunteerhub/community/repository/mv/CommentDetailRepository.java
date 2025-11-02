package com.volunteerhub.community.repository.mv;

import com.volunteerhub.community.entity.mv.CommentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDetailRepository extends JpaRepository<CommentDetail, Long> {
    Page<CommentDetail> findByPostId(Long postId, Pageable pageable);
}
