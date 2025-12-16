package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.EditUserProfile;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IUserProfileService {
    ModerationResponse editUserProfile(UUID userId, EditUserProfile input);

    ModerationResponse createUserProfile(UUID userId, EditUserProfile input);
}
