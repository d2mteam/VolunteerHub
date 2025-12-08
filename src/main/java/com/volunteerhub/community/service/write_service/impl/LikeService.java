package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.redis_service.RedisLikeService;
import com.volunteerhub.community.service.write_service.ILikeService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService implements ILikeService {

    private final RedisLikeService redisLikeService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ActionResponse<Void> like(UUID userId, Long targetId, String targetType) {
        TableType.valueOf(targetType); // validate enum, sẽ throw nếu sai

        Long likeId = idGenerator.nextId();
        redisLikeService.like(targetId, targetType, userId, likeId);
        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                likeId.toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> unlike(UUID userId, Long targetId, String targetType) {
        TableType.valueOf(targetType); // validate enum

        Long likeId = redisLikeService.unlike(targetId, targetType, userId);
        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                likeId != null ? likeId.toString() : targetId.toString(),
                now,
                now
        );
    }
}
