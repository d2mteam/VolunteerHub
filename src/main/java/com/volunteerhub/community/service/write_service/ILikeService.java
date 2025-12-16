package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;


public interface ILikeService {
    ModerationResponse like(UUID userId, Long targetId, String targetType);
    ModerationResponse unlike(UUID userId, Long targetId, String targetType);
}
