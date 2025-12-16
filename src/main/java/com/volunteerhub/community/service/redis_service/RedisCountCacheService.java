package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisCountCacheService {
    private final StringRedisTemplate stringRedisTemplate;

    public List<Integer> get(List<String> keys) {
        List<String> rawValues;

        try {
            rawValues = stringRedisTemplate.opsForValue().multiGet(keys);
        } catch (RedisConnectionFailureException e) {
            return null;
        }

        if (rawValues == null) {
            return null;
        }

        return rawValues.stream()
                .map(v -> v == null ? null : Integer.parseInt(v))
                .toList();
    }


    public void set(List<String> keys, List<Integer> values, Duration ttl) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("keys and values size mismatch");
        }

        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (int i = 0; i < keys.size(); i++) {
                byte[] key = stringRedisTemplate.getStringSerializer().serialize(keys.get(i));
                byte[] value = stringRedisTemplate.getStringSerializer()
                        .serialize(String.valueOf(values.get(i)));

                connection.stringCommands().set(key, value);
                connection.keyCommands().expire(key, ttl.getSeconds());
            }
            return null;
        });
    }
}
