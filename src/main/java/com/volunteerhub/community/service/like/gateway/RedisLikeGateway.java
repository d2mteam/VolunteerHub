package com.volunteerhub.community.service.like.gateway;

import com.volunteerhub.community.model.db_enum.TableType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class RedisLikeGateway {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String streamKey = "stream:like-events";

    public RedisLikeGateway(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public LikeGatewayResult like(TableType tableType, Long targetId, UUID userId) {
        DefaultRedisScript<List> script = new DefaultRedisScript<>(LikeLuaScripts.LIKE_SCRIPT, List.class);
        List<String> keys = Arrays.asList(membershipKey(tableType, targetId, userId), countKey(tableType, targetId), streamKey);
        List<?> result = (List<?>) redisTemplate.execute(script, keys, tableType.name(), targetId.toString(), userId.toString());
        return toResult(result);
    }

    public LikeGatewayResult unlike(TableType tableType, Long targetId, UUID userId) {
        DefaultRedisScript<List> script = new DefaultRedisScript<>(LikeLuaScripts.UNLIKE_SCRIPT, List.class);
        List<String> keys = Arrays.asList(membershipKey(tableType, targetId, userId), countKey(tableType, targetId), streamKey);
        List<?> result = (List<?>) redisTemplate.execute(script, keys, tableType.name(), targetId.toString(), userId.toString());
        return toResult(result);
    }

    public String getStreamKey() {
        return streamKey;
    }

    private LikeGatewayResult toResult(List<?> raw) {
        if (raw == null || raw.size() < 2) {
            return new LikeGatewayResult(false, 0);
        }
        boolean applied = Objects.equals(raw.get(0), Long.valueOf(1)) || Objects.equals(raw.get(0), 1L) || Objects.equals(raw.get(0), 1);
        long count = ((Number) raw.get(1)).longValue();
        return new LikeGatewayResult(applied, count);
    }

    private String membershipKey(TableType tableType, Long targetId, UUID userId) {
        return String.format("like:%s:%s:%s", tableType.name(), targetId, userId);
    }

    private String countKey(TableType tableType, Long targetId) {
        return String.format("likeCount:%s:%s", tableType.name(), targetId);
    }

    public record LikeGatewayResult(boolean applied, long likeCount) {
    }
}
