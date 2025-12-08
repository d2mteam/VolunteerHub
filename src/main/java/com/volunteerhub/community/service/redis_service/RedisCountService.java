package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCountService {

    private final RedisLikeService redisLikeService;

    public int likeCount(Long targetId, String targetType) {
        return Math.toIntExact(redisLikeService.likeCount(targetId, targetType));
    }

    public int commentCount(Long postId) {
        return -1;
    }

    public int memberCount(Long eventId) {
        return -1;
    }

    public int postCount(Long eventId) {
        return -1;
    }
}
