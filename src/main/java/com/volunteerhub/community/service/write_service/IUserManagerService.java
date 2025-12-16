package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IUserManagerService {
    ModerationResponse banUser(UUID userId);

    ModerationResponse unbanUser(UUID userId);
}