package com.volunteerhub.community.repository.mv;

import com.volunteerhub.community.entity.mv.PostDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostDetailRepository extends JpaRepository<PostDetail, Long> {
    Page<PostDetail> findByEventId(Long eventId, Pageable pageable);

}
