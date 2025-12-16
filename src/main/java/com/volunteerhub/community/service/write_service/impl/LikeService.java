package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationStatus;
import com.volunteerhub.community.dto.ModerationTargetType;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.cache.CounterInvalidationService;
import com.volunteerhub.community.service.redis_service.RedisLikeService;
import com.volunteerhub.community.service.write_service.ILikeService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService implements ILikeService {

    private final RedisLikeService redisLikeService;
    private final SnowflakeIdGenerator idGenerator;
    private final CounterInvalidationService counterInvalidationService;

    @Override
    public ModerationResponse like(UUID userId, Long targetId, String targetType) {
        TableType.valueOf(targetType); // validate enum, sẽ throw nếu sai

        Long likeId = idGenerator.nextId();
        redisLikeService.like(targetId, targetType, userId, likeId);
        counterInvalidationService.evictLike(TableType.valueOf(targetType), targetId);
        return ModerationResponse.success(
                ModerationAction.LIKE_TARGET,
                ModerationTargetType.LIKE,
                likeId.toString(),
                ModerationStatus.LIKED,
                "Target liked"
        );
    }

    @Override
    public ModerationResponse unlike(UUID userId, Long targetId, String targetType) {
        TableType.valueOf(targetType); // validate enum

        Long likeId = redisLikeService.unlike(targetId, targetType, userId);
        counterInvalidationService.evictLike(TableType.valueOf(targetType), targetId);
        return ModerationResponse.success(
                ModerationAction.UNLIKE_TARGET,
                ModerationTargetType.LIKE,
                likeId != null ? likeId.toString() : targetId.toString(),
                ModerationStatus.UNLIKED,
                "Target unliked"
        );
    }
}
