package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.syn_queue_key}")
    private String syncQueueKey;

    public void like(Long targetId, String tableType, UUID userId) {
    }

    public void unlike(Long targetId, String tableType, UUID userId) {
    }

    private String generateLikeKey(Long targetId, String tableType) {
        return "like:" + tableType + ":" + targetId;
    }
}
