package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    UserProfile findByUserId(UUID userId);
}
