package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.EditUserProfileInput;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IUserProfileService {
    ModerationResponse editUserProfile(UUID userId, EditUserProfileInput input);

    ModerationResponse createUserProfile(UUID userId, EditUserProfileInput input);
}
