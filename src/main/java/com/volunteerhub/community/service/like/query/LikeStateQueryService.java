package com.volunteerhub.community.service.like.query;

import com.volunteerhub.community.service.like.model.LikeTargetKey;
import com.volunteerhub.community.service.like.repository.LikeReadRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LikeStateQueryService {
    private final RedisLikeStateReader redisLikeStateReader;
    private final LikeReadRepository likeReadRepository;

    public LikeStateQueryService(RedisLikeStateReader redisLikeStateReader, LikeReadRepository likeReadRepository) {
        this.redisLikeStateReader = redisLikeStateReader;
        this.likeReadRepository = likeReadRepository;
    }

    public Map<LikeTargetKey, Boolean> hasUserLikedBatch(UUID userId, List<LikeTargetKey> targets) {
        Map<LikeTargetKey, Boolean> redisResult = new HashMap<>(redisLikeStateReader.hasUserLiked(userId, targets));
        if (redisResult.size() == targets.size()) {
            return redisResult;
        }
        Map<LikeTargetKey, Boolean> dbResult = likeReadRepository.fetchMembership(userId, targets);
        dbResult.forEach(redisResult::putIfAbsent);
        return redisResult;
    }
}
