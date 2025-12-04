package com.volunteerhub.community.repository;

import com.volunteerhub.community.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    boolean existsByUsername(String username);
    Optional<UserProfile> findByUsername(String username);
}
