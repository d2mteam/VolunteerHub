package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.EditUserProfileInput;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IUserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserProfileService implements IUserProfileService {
    private final UserProfileRepository userProfileRepository;

    @Override
    public void editUserProfile(UUID userId, EditUserProfileInput input) {
        userProfileRepository.findById(userId).ifPresent(userProfile -> {
            userProfile.setEmail(input.getEmail());
            userProfile.setFullName(input.getFullName());
            userProfile.setAvatarUrl(input.getAvatarUrl());
        });
    }
}
