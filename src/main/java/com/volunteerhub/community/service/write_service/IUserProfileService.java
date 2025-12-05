package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.EditUserProfile;
import com.volunteerhub.community.dto.ActionResponse;

import java.util.UUID;

public interface IUserProfileService {
    ActionResponse<Void> editUserProfile(UUID userId, EditUserProfile input);

    ActionResponse<Void> createUserProfile(UUID userId, EditUserProfile input);
}
