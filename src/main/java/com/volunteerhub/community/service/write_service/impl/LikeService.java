package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.entity.Like;
import com.volunteerhub.community.entity.TableType;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.ILikeService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public void like(UUID userId, Long targetId, String targetType) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        likeRepository.save(Like.builder()
                .likeId(idGenerator.nextId())
                .targetId(targetId)
                .tableType(TableType.valueOf(targetType))
                .createdBy(userProfile)
                .build());
    }

    @Override
    public void unLike(UUID userId, Long likeId) {
        likeRepository.deleteById(likeId);
    }
}
