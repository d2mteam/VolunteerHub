package com.volunteerhub.community.cache_model;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@RedisHash("user_profile_read")
public class UserProfile {
    @Id
    private String userId;

    @Indexed
    private String username;

    private String email;
    private String avatar;
    private List<String> roles;

    private LocalDateTime createdAt;
}
