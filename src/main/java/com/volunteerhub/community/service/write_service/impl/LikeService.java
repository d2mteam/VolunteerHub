package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.model.table.Like;
import com.volunteerhub.community.model.table.UserProfile;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ActionResponse<Void> like(UUID userId, Long targetId, String targetType) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);

        Like like = Like.builder()
                .likeId(idGenerator.nextId())
                .targetId(targetId)
                .tableType(TableType.valueOf(targetType))
                .createdBy(userProfile)
                .build();

        likeRepository.save(like);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                like.getLikeId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> unlike(UUID userId, Long targetId, String targetType) {
        Optional<Like> existing = likeRepository.findByTargetIdAndTableType(targetId, TableType.valueOf(targetType));
        if (existing.isEmpty()) {
            return ActionResponse.failure("Like not found");
        }

        likeRepository.deleteById(existing.get().getLikeId());

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                existing.get().getLikeId().toString(),
                now,
                now
        );
    }
}
