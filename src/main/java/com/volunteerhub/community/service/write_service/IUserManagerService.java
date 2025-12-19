package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;

import java.util.UUID;

public interface IUserManagerService {
    ModerationResponse banUser(UUID userId);

    ModerationResponse unbanUser(UUID userId);
}

