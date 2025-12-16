package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.Like;
import com.volunteerhub.community.model.db_enum.TableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByTargetIdAndTableType(Long targetId, TableType tableType);

    boolean existsByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    Optional<Like> findByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    @Query("SELECT l.targetId AS targetId, COUNT(l) AS count " +
            "FROM Like l " +
            "WHERE l.targetId IN :targetIds AND l.tableType = :tableType " +
            "GROUP BY l.targetId")
    List<TargetCountProjection> countByTargetIdsAndType(@Param("targetIds") Collection<Long> targetIds,
                                                        @Param("tableType") TableType tableType);
}

public interface TargetCountProjection {
    Long getTargetId();

    Long getCount();
}
