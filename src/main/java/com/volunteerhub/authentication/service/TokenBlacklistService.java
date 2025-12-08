package com.volunteerhub.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String KEY_PREFIX = "token:blacklist:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void blacklist(String jti, long ttlMillis) {
        if (jti == null || ttlMillis <= 0) return;

        redisTemplate.opsForValue()
                .set(KEY_PREFIX + jti, true, Duration.ofMillis(ttlMillis));
    }

    public boolean isBlacklisted(String jti) {
        return jti != null && Boolean.TRUE.equals(
                redisTemplate.opsForValue().get(KEY_PREFIX + jti)
        );
    }
}
