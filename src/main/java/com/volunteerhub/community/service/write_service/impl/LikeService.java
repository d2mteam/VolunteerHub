package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.rest.response.ModerationAction;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.response.ModerationStatus;
import com.volunteerhub.community.dto.rest.response.ModerationTargetType;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Like;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
import com.volunteerhub.community.service.write_service.ILikeService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;
    private final UserProfileRepository  userProfileRepository;
    private final RedisCounterService redisCounterService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ModerationResponse like(UUID userId, Long targetId, String targetType) {
        long likeId = idGenerator.nextId();
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Like like = Like.builder()
                .tableType(TableType.valueOf(targetType))
                .likeId(idGenerator.nextId())
                .targetId(targetId)
                .createdBy(userProfile)
                .build();

        likeRepository.save(like);
        updateCountersOnLike(TableType.valueOf(targetType), targetId, true);

        return ModerationResponse.success(
                ModerationAction.LIKE_TARGET,
                ModerationTargetType.LIKE,
                Long.toString(likeId),
                ModerationStatus.LIKED,
                "Target liked"
        );
    }

    @Override
    public ModerationResponse unlike(UUID userId, Long targetId, String targetType) {
        TableType.valueOf(targetType); // validate enum
        userProfileRepository.getReferenceById(userId);
        Optional<Like> like = likeRepository.findByCreatedBy_UserIdAndTargetIdAndTableType(userId,targetId,TableType.valueOf(targetType));
        like.ifPresent(likeRepository::delete);
        like.ifPresent(found -> updateCountersOnLike(found.getTableType(), targetId, false));
        return ModerationResponse.success(
                ModerationAction.UNLIKE_TARGET,
                ModerationTargetType.LIKE,
                like.map(found -> found.getLikeId().toString()).orElse(""),
                ModerationStatus.UNLIKED,
                "Target unliked"
        );
    }

    private void updateCountersOnLike(TableType tableType, Long targetId, boolean isLike) {
        long delta = isLike ? 1 : -1;
        switch (tableType) {
            case EVENT -> {
                redisCounterService.incrementEventLikeCount(targetId, delta);
                redisCounterService.updateEventLatestInteractionAt(targetId, LocalDateTime.now());
            }
            case POST -> redisCounterService.incrementPostLikeCount(targetId, delta);
            case COMMENT -> redisCounterService.incrementCommentLikeCount(targetId, delta);
            default -> {
            }
        }
    }
}
