package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RedisLikeService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.like-event}")
    private String redisLikeEvent;

    public void like(Long targetId, String tableType, UUID userId) {
        String key = buildKey(tableType, targetId);

        // 1) Update Redis Set
        redisTemplate.opsForSet().add(key, userId.toString());

        // 2) Push event vào Redis Stream
        Map<String, Object> fields = new HashMap<>();
        fields.put("action", "LIKE");
        fields.put("tableType", tableType);
        fields.put("targetId", targetId);
        fields.put("userId", userId.toString());
        fields.put("timestamp", System.currentTimeMillis());

        redisTemplate.opsForStream().add(redisLikeEvent, fields);
    }

    public void unlike(Long targetId, String tableType, UUID userId) {
        String key = buildKey(tableType, targetId);

        redisTemplate.opsForSet().remove(key, userId.toString());

        Map<String, Object> fields = new HashMap<>();
        fields.put("action", "UNLIKE");
        fields.put("tableType", tableType);
        fields.put("targetId", targetId);
        fields.put("userId", userId.toString());
        fields.put("timestamp", System.currentTimeMillis());

        redisTemplate.opsForStream().add(redisLikeEvent, fields);
    }

    private String buildKey(String tableType, Long targetId) {
        return String.format("likes:%s:%d", tableType, targetId);
    }
}


//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import java.util.Arrays;
//import java.util.UUID;
//
//public void likeOrUnlike(Long targetId, String tableType, UUID userId, boolean like) {
//    String setKey = "likes:" + tableType + ":" + targetId;
//    String streamKey = "like_events";
//
//    String luaScript = """
//        if ARGV[2] == "LIKE" then
//            redis.call('SADD', KEYS[1], ARGV[1])
//        elseif ARGV[2] == "UNLIKE" then
//            redis.call('SREM', KEYS[1], ARGV[1])
//        end
//        local fields = {'action', ARGV[2], 'tableType', ARGV[3], 'targetId', ARGV[4], 'userId', ARGV[1], 'timestamp', ARGV[5]}
//        redis.call('XADD', KEYS[2], '*', unpack(fields))
//        return 1
//    """;
//
//    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//    redisScript.setScriptText(luaScript);
//    redisScript.setResultType(Long.class);
//
//    redisTemplate.execute(
//            redisScript,
//            Arrays.asList(setKey, streamKey),
//            userId.toString(),
//            like ? "LIKE" : "UNLIKE",
//            tableType,
//            targetId.toString(),
//            String.valueOf(System.currentTimeMillis())
//    );
//}