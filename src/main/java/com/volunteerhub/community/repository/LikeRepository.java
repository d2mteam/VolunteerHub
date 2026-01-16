package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.model.db_enum.TableType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    Optional<Like> findByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    @Query("""
        select count(l)
        from Like l
        where l.targetId = :targetId
          and l.tableType = :tableType
    """)
    int countByTargetIdAndTableType(
            @Param("targetId") Long targetId,
            @Param("tableType") TableType tableType
    );

    @Query("""
        select l.targetId as targetId, count(l) as count
        from Like l
        where l.tableType = :tableType
          and l.targetId in :targetIds
        group by l.targetId
    """)
    List<LikeCountView> countByTargetIdsAndTableType(
            @Param("targetIds") List<Long> targetIds,
            @Param("tableType") TableType tableType
    );

    interface LikeCountView {
        Long getTargetId();

        Long getCount();
    }
}
