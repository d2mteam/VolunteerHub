package com.volunteerhub.community.repository;

import com.volunteerhub.community.entity.UserAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {
    Optional<UserAuthProvider> findByProviderAndProviderUserId(UserAuthProvider provider, String providerUserId);
}
