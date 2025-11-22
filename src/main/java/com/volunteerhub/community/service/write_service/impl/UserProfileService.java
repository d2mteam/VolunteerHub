package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.EditUserProfileInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.model.table.UserProfile;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public ActionResponse<Void> editUserProfile(UUID userId, EditUserProfileInput input) {
        Optional<UserProfile> optional = userProfileRepository.findById(userId);
        if (optional.isEmpty()) {
            return ActionResponse.failure("User profile not found");
        }

        UserProfile userProfile = optional.get();
        userProfile.setEmail(input.getEmail());
        userProfile.setFullName(input.getFullName());
        userProfile.setAvatarUrl(input.getAvatarUrl());
        userProfileRepository.save(userProfile);

        return ActionResponse.success(
                userProfile.getUserId().toString(),
                userProfile.getCreatedAt(),
                LocalDateTime.now()
        );
    }
}
