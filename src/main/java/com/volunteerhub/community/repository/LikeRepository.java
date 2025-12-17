package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.projection.LikeCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    Optional<Like> findByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    long countByTargetIdAndTableType(Long targetId, TableType tableType);

    @Query("SELECT l.targetId as targetId, COUNT(l) as count FROM Like l WHERE l.tableType = :tableType AND l.targetId IN :targetIds GROUP BY l.targetId")
    List<LikeCountProjection> countByTableTypeAndTargetIdIn(@Param("tableType") TableType tableType,
                                                           @Param("targetIds") Collection<Long> targetIds);
}
