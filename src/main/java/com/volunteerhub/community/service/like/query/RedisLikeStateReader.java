package com.volunteerhub.community.service.like.query;

import com.volunteerhub.community.service.like.model.LikeTargetKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisLikeStateReader {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLikeStateReader(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<LikeTargetKey, Boolean> hasUserLiked(UUID userId, List<LikeTargetKey> targets) {
        if (targets.isEmpty()) {
            return Map.of();
        }
        List<String> keys = targets.stream()
                .map(target -> membershipKey(target, userId))
                .toList();
        List<Object> raw = redisTemplate.opsForValue().multiGet(keys);
        Map<LikeTargetKey, Boolean> result = new HashMap<>();
        for (int i = 0; i < targets.size(); i++) {
            Object value = raw != null && i < raw.size() ? raw.get(i) : null;
            result.put(targets.get(i), value != null);
        }
        return result;
    }

    private String membershipKey(LikeTargetKey target, UUID userId) {
        return String.format("like:%s:%s:%s", target.getTableType().name(), target.getTargetId(), userId);
    }
}
