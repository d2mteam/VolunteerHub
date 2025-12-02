package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.EditUserProfileInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.UUID;

public interface IUserProfileService {
    ActionResponse<Void> editUserProfile(UUID userId, EditUserProfileInput input);

    ActionResponse<Void> createUserProfile(UUID userId, EditUserProfileInput input);
}
