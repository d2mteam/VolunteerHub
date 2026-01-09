package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.rest.response.ModerationAction;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.response.ModerationResult;
import com.volunteerhub.community.dto.rest.response.ModerationStatus;
import com.volunteerhub.community.dto.rest.response.ModerationTargetType;
import com.volunteerhub.community.dto.rest.request.EditUserProfileInput;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserProfileService;
import com.volunteerhub.media.model.MediaRefType;
import com.volunteerhub.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final MediaService mediaService;

    @Override
    public ModerationResponse editUserProfile(UUID userId, EditUserProfileInput input) {
        Optional<UserProfile> optional = userProfileRepository.findById(userId);
        if (optional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.EDIT_USER_PROFILE,
                    ModerationTargetType.USER_PROFILE,
                    userId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "User profile not found",
                    "USER_PROFILE_NOT_FOUND"
            );
        }

        UserProfile userProfile = optional.get();
        userProfile.setEmail(input.getEmail());
        userProfile.setUsername(input.getUsername());
        userProfile.setFullName(input.getFullName());
        userProfile.setBio(input.getBio());
        userProfile.setAvatarId(input.getAvatarId());

        userProfileRepository.save(userProfile);
        syncAvatarMedia(userId, input.getAvatarId());

        return ModerationResponse.success(
                ModerationAction.EDIT_USER_PROFILE,
                ModerationTargetType.USER_PROFILE,
                userProfile.getUserId().toString(),
                ModerationStatus.PROFILE_UPDATED,
                "User profile updated"
        );
    }

    @Override
    public ModerationResponse createUserProfile(UUID userId, EditUserProfileInput input) {
        if (userProfileRepository.existsById(userId)) {
            return ModerationResponse.failure(
                    ModerationAction.CREATE_USER_PROFILE,
                    ModerationTargetType.USER_PROFILE,
                    userId.toString(),
                    ModerationResult.INVALID,
                    ModerationStatus.DENIED,
                    "User profile already exists",
                    "USER_PROFILE_EXISTS"
            );
        }

        UserProfile userProfile = UserProfile.builder()
                .userId(userId)
                .email(input.getEmail())
                .username(input.getUsername())
                .fullName(input.getFullName())
                .bio(input.getBio())
                .avatarId(input.getAvatarId())
                .build();

        userProfileRepository.save(userProfile);
        syncAvatarMedia(userId, input.getAvatarId());

        return ModerationResponse.success(
                ModerationAction.CREATE_USER_PROFILE,
                ModerationTargetType.USER_PROFILE,
                userProfile.getUserId().toString(),
                ModerationStatus.PROFILE_CREATED,
                "User profile created"
        );
    }

    private void syncAvatarMedia(UUID userId, String avatarId) {
        if (avatarId == null || avatarId.isBlank()) {
            mediaService.syncMediaResources(userId, MediaRefType.USER, userId, List.of());
            return;
        }
        UUID resourceId = UUID.fromString(avatarId);
        mediaService.syncMediaResources(userId, MediaRefType.USER, userId, List.of(resourceId));
    }
}
