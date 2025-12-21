package com.volunteerhub.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserBanCacheService {
    private static final String KEY_PREFIX = "user:ban-status:";
    private static final String STATUS_BANNED = "BANNED";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final RedisTemplate<String, Object> redisTemplate;

    public void markBanned(UUID userId) {
        setStatus(userId, STATUS_BANNED);
    }

    public void markUnbanned(UUID userId) {
        setStatus(userId, STATUS_ACTIVE);
    }

    public boolean isBanned(UUID userId) {
        if (userId == null) return false;
        Object value = redisTemplate.opsForValue().get(key(userId));
        return STATUS_BANNED.equals(value);
    }

    private void setStatus(UUID userId, String status) {
        if (userId == null) return;
        redisTemplate.opsForValue().set(key(userId), status);
    }

    private String key(UUID userId) {
        return KEY_PREFIX + userId;
    }
}
