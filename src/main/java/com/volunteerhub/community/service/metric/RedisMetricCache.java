package com.volunteerhub.community.service.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisMetricCache {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.metrics.ttl-seconds:3600}")
    private long ttlSeconds;

    public Map<Long, Optional<Long>> multiGet(List<Long> ids, MetricDescriptor<?, ?> descriptor) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        List<String> keys = ids.stream()
                .map(descriptor::redisKey)
                .toList();

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        Map<Long, Optional<Long>> results = new HashMap<>();
        if (values == null) {
            ids.forEach(id -> results.put(id, Optional.empty()));
            return results;
        }

        for (int i = 0; i < ids.size(); i++) {
            Object value = values.get(i);
            results.put(ids.get(i), parseValue(value));
        }

        return results;
    }

    public void multiSet(Map<Long, Long> values, MetricDescriptor<?, ?> descriptor) {
        if (values.isEmpty()) {
            return;
        }

        Duration ttl = Duration.ofSeconds(ttlSeconds);
        values.forEach((id, value) ->
                redisTemplate.opsForValue().set(descriptor.redisKey(id), value, ttl)
        );
    }

    private Optional<Long> parseValue(Object value) {
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}
