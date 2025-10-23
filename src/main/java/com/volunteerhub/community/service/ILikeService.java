package com.volunteerhub.community.service;

import java.util.UUID;

public interface ILikeActionService {
    void like(UUID userId, Long targetId, String targetType);
    void unLike(UUID userId, Long likeId);
}
