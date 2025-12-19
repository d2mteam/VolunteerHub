package com.volunteerhub.community.service.like.query;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.like.repository.LikeReadRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LikeCountQueryService {
    private final RedisLikeCountReader redisLikeCountReader;
    private final LikeReadRepository likeReadRepository;

    public LikeCountQueryService(RedisLikeCountReader redisLikeCountReader, LikeReadRepository likeReadRepository) {
        this.redisLikeCountReader = redisLikeCountReader;
        this.likeReadRepository = likeReadRepository;
    }

    public Map<Long, Long> likeCountsBatch(TableType tableType, List<Long> targetIds) {
        Map<Long, Long> redisCounts = new HashMap<>(redisLikeCountReader.fetchCounts(tableType, targetIds));
        if (redisCounts.size() == targetIds.size()) {
            return redisCounts;
        }
        Map<Long, Long> dbCounts = likeReadRepository.countByTargets(tableType, targetIds);
        dbCounts.forEach((id, count) -> redisCounts.putIfAbsent(id, count));
        return redisCounts;
    }
}
