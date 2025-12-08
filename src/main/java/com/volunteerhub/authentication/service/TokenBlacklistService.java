package com.volunteerhub.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Simple token blacklist backed by Redis. Stores token jti with TTL matching the remaining token lifetime.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String KEY_PREFIX = "token:blacklist:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void blacklist(String jti, long remainingMillis) {
        if (jti == null || remainingMillis <= 0) {
            return;
        }
        redisTemplate.opsForValue().set(key(jti), Boolean.TRUE, Duration.ofMillis(remainingMillis));
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        Boolean exists = redisTemplate.hasKey(key(jti));
        return Boolean.TRUE.equals(exists);
    }

    private String key(String jti) {
        return KEY_PREFIX + jti;
    }
}
