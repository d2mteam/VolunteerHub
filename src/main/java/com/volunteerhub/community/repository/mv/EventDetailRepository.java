package com.volunteerhub.community.repository.mv;

import com.volunteerhub.community.entity.mv.EventDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EventDetailRepository extends JpaRepository<EventDetail, Long> {
    @Query(value = """
            SELECT e.*
            FROM event_detail_mv e
            JOIN role_in_event rie ON rie.event_id = e.event_id
            WHERE rie.user_profile_id = :userId
            """,
            countQuery = """
                    SELECT count(*)
                    FROM role_in_event rie
                    WHERE rie.user_profile_id = :userId
                    """,
            nativeQuery = true)
    Page<EventDetail> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);
}
