package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.rest.request.EditUserProfile;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.model.UserProfile;
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
    public ActionResponse<Void> editUserProfile(UUID userId, EditUserProfile input) {
        Optional<UserProfile> optional = userProfileRepository.findById(userId);
        if (optional.isEmpty()) {
            return ActionResponse.failure("User profile not found");
        }

        UserProfile userProfile = optional.get();
        userProfile.setEmail(input.getEmail());
        userProfile.setFullName(input.getFullName());
        userProfileRepository.save(userProfile);

        return ActionResponse.success(
                userProfile.getUserId().toString(),
                null,
                LocalDateTime.now()
        );
    }

    @Override
    public ActionResponse<Void> createUserProfile(UUID userId, EditUserProfile input) {
        if (userProfileRepository.existsById(userId)) {
            return ActionResponse.failure("User profile already exists");
        }

        UserProfile userProfile = UserProfile.builder()
                .userId(userId)
                .email(input.getEmail())
                .fullName(input.getFullName())
                .build();

        userProfileRepository.save(userProfile);

        return ActionResponse.success(
                userProfile.getUserId().toString(),
                userProfile.getCreatedAt(),
                LocalDateTime.now()
        );
    }
}
