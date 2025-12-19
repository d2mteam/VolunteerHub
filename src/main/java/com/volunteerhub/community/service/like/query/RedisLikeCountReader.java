package com.volunteerhub.community.service.like.query;

import com.volunteerhub.community.model.db_enum.TableType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisLikeCountReader {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisLikeCountReader(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<Long, Long> fetchCounts(TableType tableType, List<Long> targetIds) {
        if (targetIds.isEmpty()) {
            return Map.of();
        }
        List<String> keys = targetIds.stream()
                .map(id -> countKey(tableType, id))
                .toList();
        List<Object> raw = redisTemplate.opsForValue().multiGet(keys);
        Map<Long, Long> result = new HashMap<>();
        for (int i = 0; i < targetIds.size(); i++) {
            Object value = raw != null && i < raw.size() ? raw.get(i) : null;
            long count = value == null ? 0 : Long.parseLong(value.toString());
            result.put(targetIds.get(i), count);
        }
        return result;
    }

    private String countKey(TableType tableType, Long targetId) {
        return String.format("likeCount:%s:%s", tableType.name(), targetId);
    }
}
