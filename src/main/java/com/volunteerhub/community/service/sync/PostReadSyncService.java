package com.volunteerhub.community.service.sync;

import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.PostReadRepository;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostReadSyncService {
    private final RedisCounterService redisCounterService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostReadRepository postReadRepository;

    @Scheduled(fixedDelayString = "${readmodel.post-read-sync-ms:30000}")
    @Transactional
    public void sync() {
        Set<Object> dirtyIds = redisTemplate.opsForSet().members(redisCounterService.postDirtyKey());
        if (dirtyIds == null || dirtyIds.isEmpty()) {
            return;
        }

        for (Object rawId : dirtyIds) {
            Long postId;
            try {
                postId = Long.valueOf(rawId.toString());
            } catch (NumberFormatException ex) {
                redisTemplate.opsForSet().remove(redisCounterService.postDirtyKey(), rawId);
                continue;
            }

            PostRead postRead = postReadRepository.findById(postId).orElse(null);
            if (postRead == null) {
                redisTemplate.opsForSet().remove(redisCounterService.postDirtyKey(), rawId);
                continue;
            }

            postRead.setCommentCount(redisCounterService.getPostCommentCount(postId).orElse(postRead.getCommentCount()));
            postRead.setLikeCount(redisCounterService.getPostLikeCount(postId).orElse(postRead.getLikeCount()));
            postRead.setUpdatedAt(LocalDateTime.now());
            postReadRepository.save(postRead);

            redisTemplate.opsForSet().remove(redisCounterService.postDirtyKey(), rawId);
        }
    }
}
