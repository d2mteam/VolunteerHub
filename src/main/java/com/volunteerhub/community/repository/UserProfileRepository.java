package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.model.db_enum.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    @Modifying
    @Query("UPDATE UserProfile u SET u.status = :status WHERE u.userId = :userId")
    int updateStatus(@Param("userId") UUID userId, @Param("status") UserStatus status);
}
