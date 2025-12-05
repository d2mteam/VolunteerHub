package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.ActionResponse;

import java.util.UUID;

public interface IUserManagerService {
    ActionResponse<Void> banUser(UUID userId);

    ActionResponse<Void> unbanUser(UUID userId);
}