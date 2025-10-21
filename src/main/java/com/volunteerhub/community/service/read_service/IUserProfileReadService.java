package com.volunteerhub.community.service.read_service;

import com.volunteerhub.community.read_model.UserProfileRead;

import java.util.UUID;

public interface IUserProfileReadService {
    UserProfileRead getUserProfileById(UUID id);
}
