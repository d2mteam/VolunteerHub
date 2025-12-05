package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.ActionResponse;

import java.util.UUID;


public interface ILikeService {
    ActionResponse<Void> like(UUID userId, Long targetId, String targetType);
    ActionResponse<Void> unlike(UUID userId, Long targetId, String targetType);
}
