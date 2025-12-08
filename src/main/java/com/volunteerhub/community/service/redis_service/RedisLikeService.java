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

    /**
     * Lưu lượt thích vào Redis (set để đếm/kiểm tra, hash để giữ likeId) rồi đẩy event vào stream.
     */
    public Long like(Long targetId, String tableType, UUID userId, Long likeId) {
        String setKey = buildSetKey(tableType, targetId);
        String hashKey = buildHashKey(tableType, targetId);

        redisTemplate.opsForSet().add(setKey, userId.toString());
        redisTemplate.opsForHash().put(hashKey, userId.toString(), likeId.toString());

        redisTemplate.opsForStream().add(redisLikeEvent, buildEventFields("LIKE", tableType, targetId, userId, likeId));
        return likeId;
    }

    /**
     * Xóa lượt thích khỏi Redis trước, sau đó đẩy event để worker đồng bộ DB.
     */
    public Long unlike(Long targetId, String tableType, UUID userId) {
        String setKey = buildSetKey(tableType, targetId);
        String hashKey = buildHashKey(tableType, targetId);

        Object rawLikeId = redisTemplate.opsForHash().get(hashKey, userId.toString());
        Long likeId = rawLikeId != null ? Long.valueOf(rawLikeId.toString()) : null;

        redisTemplate.opsForSet().remove(setKey, userId.toString());
        redisTemplate.opsForHash().delete(hashKey, userId.toString());

        redisTemplate.opsForStream().add(redisLikeEvent, buildEventFields("UNLIKE", tableType, targetId, userId, likeId));
        return likeId;
    }

    public boolean hasUserLiked(Long targetId, String tableType, UUID userId) {
        Boolean member = redisTemplate.opsForSet().isMember(buildSetKey(tableType, targetId), userId.toString());
        return Boolean.TRUE.equals(member);
    }

    public long likeCount(Long targetId, String tableType) {
        Long size = redisTemplate.opsForSet().size(buildSetKey(tableType, targetId));
        return size == null ? 0 : size;
    }

    private String buildSetKey(String tableType, Long targetId) {
        return String.format("likes:%s:%d", tableType, targetId);
    }

    private String buildHashKey(String tableType, Long targetId) {
        return String.format("likeIds:%s:%d", tableType, targetId);
    }

    private Map<String, Object> buildEventFields(String action, String tableType, Long targetId, UUID userId, Long likeId) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("action", action);
        fields.put("tableType", tableType);
        fields.put("targetId", targetId);
        fields.put("userId", userId.toString());
        if (likeId != null) {
            fields.put("likeId", likeId);
        }
        fields.put("timestamp", System.currentTimeMillis());
        return fields;
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
