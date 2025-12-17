package com.volunteerhub.community.service.redis_service;

import com.volunteerhub.community.model.db_enum.TableType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisEngagementViewService {
    private final RedisTemplate<String, Object> redisTemplate;

    public Long getLikeCount(TableType tableType, Long targetId) {
        Object raw = redisTemplate.opsForHash().get(buildKey(tableType, targetId), "likeCount");
        return raw == null ? null : Long.parseLong(raw.toString());
    }

    public void setLikeCount(TableType tableType, Long targetId, long likeCount) {
        redisTemplate.opsForHash().put(buildKey(tableType, targetId), "likeCount", likeCount);
    }

    public void incrementLikeCount(TableType tableType, Long targetId) {
        redisTemplate.opsForHash().increment(buildKey(tableType, targetId), "likeCount", 1);
    }

    public void decrementLikeCount(TableType tableType, Long targetId) {
        Long newValue = redisTemplate.opsForHash().increment(buildKey(tableType, targetId), "likeCount", -1);
        if (newValue != null && newValue < 0) {
            redisTemplate.opsForHash().put(buildKey(tableType, targetId), "likeCount", 0);
        }
    }

    public Map<Long, Long> getLikeCounts(TableType tableType, Collection<Long> targetIds) {
        Map<Long, Long> result = new HashMap<>();
        List<Object> rawCounts = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            byte[] field = "likeCount".getBytes(StandardCharsets.UTF_8);
            for (Long targetId : targetIds) {
                connection.hGet(buildKey(tableType, targetId).getBytes(StandardCharsets.UTF_8), field);
            }
            return null;
        });

        int index = 0;
        for (Long targetId : targetIds) {
            Object raw = rawCounts.get(index++);
            if (raw != null) {
                result.put(targetId, Long.parseLong(raw.toString()));
            }
        }

        return result;
    }

    public void setLikeCounts(TableType tableType, Map<Long, Long> likeCounts) {
        if (likeCounts.isEmpty()) {
            return;
        }

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Map.Entry<Long, Long> entry : likeCounts.entrySet()) {
                connection.hSet(
                        buildKey(tableType, entry.getKey()).getBytes(StandardCharsets.UTF_8),
                        "likeCount".getBytes(StandardCharsets.UTF_8),
                        String.valueOf(Math.max(entry.getValue(), 0)).getBytes(StandardCharsets.UTF_8)
                );
            }
            return null;
        });
    }

    private String buildKey(TableType tableType, Long targetId) {
        return String.format("engagement:%s:%d", tableType.name(), targetId);
    }
}
