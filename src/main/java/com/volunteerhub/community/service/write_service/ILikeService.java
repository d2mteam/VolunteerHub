package com.volunteerhub.community.service.write_service;

import java.util.UUID;

public interface ILikeService {
    void like(UUID userId, Long targetId, String targetType);

    void unLike(UUID userId, Long likeId);
}
