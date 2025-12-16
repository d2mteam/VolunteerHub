package com.volunteerhub.community.service.cache;

import com.volunteerhub.community.model.db_enum.TableType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterInvalidationService {
    private static final Logger log = LoggerFactory.getLogger(CounterInvalidationService.class);
    private final StringRedisTemplate stringRedisTemplate;

    public void evictLike(TableType type, Long targetId) {
        deleteKeys(List.of(likeCacheKey(type, targetId)));
    }

    public void evictMember(Long eventId) {
        deleteKeys(List.of(memberCacheKey(eventId)));
    }

    public void evictPostCount(Long eventId) {
        deleteKeys(List.of(postCacheKey(eventId)));
    }

    private void deleteKeys(List<String> keys) {
        try {
            stringRedisTemplate.delete(keys);
        } catch (Exception ex) {
            log.warn("Counter cache eviction skipped: {}", ex.getMessage());
        }
    }

    private String likeCacheKey(TableType type, Long id) {
        return switch (type) {
            case POST -> "post:" + id + ":likeCount";
            case COMMENT -> "comment:" + id + ":likeCount";
            case EVENT -> "event:" + id + ":likeCount";
            default -> "like:" + type.name().toLowerCase() + ":" + id;
        };
    }

    private String memberCacheKey(Long eventId) {
        return "event:" + eventId + ":memberCount";
    }

    private String postCacheKey(Long eventId) {
        return "event:" + eventId + ":postCount";
    }
}
