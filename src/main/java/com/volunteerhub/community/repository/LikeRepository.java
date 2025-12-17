package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.model.db_enum.TableType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);

    Optional<Like> findByCreatedBy_UserIdAndTargetIdAndTableType(UUID userId, Long targetId, TableType tableType);
}
