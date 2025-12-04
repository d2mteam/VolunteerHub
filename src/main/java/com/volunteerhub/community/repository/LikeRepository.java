package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.Like;
import com.volunteerhub.community.model.db_enum.TableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByTargetIdAndTableType(Long targetId, TableType tableType);
}
