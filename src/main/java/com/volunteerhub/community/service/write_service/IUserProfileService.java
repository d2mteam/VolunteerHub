package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.EditUserProfileInput;

import java.util.UUID;

public interface IUserProfileService {
    void editUserProfile(UUID userId, EditUserProfileInput input);
}
