package com.volunteerhub.community.repository;

import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.model.Like;
import com.volunteerhub.community.model.db_enum.TableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByTargetIdAndTableType(Long targetId, TableType tableType);

    boolean existsByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    Optional<Like> findByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    @Query("""
            SELECT new com.volunteerhub.community.dto.CountById(l.targetId, COUNT(l))
            FROM Like l
            WHERE l.tableType = :tableType AND l.targetId IN :targetIds
            GROUP BY l.targetId
            """)
    List<CountById> countByTargetIdsAndType(List<Long> targetIds, TableType tableType);
}
