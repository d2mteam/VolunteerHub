package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.PostReadRepository;
import com.volunteerhub.community.module.counter.RedisCounterService;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostReadService {
    private final PostReadRepository postReadRepository;
    private final UserProfileCacheService userProfileCacheService;
    private final RedisCounterService redisCounterService;

    public PostRead createFromPost(Post post) {
        var creator = userProfileCacheService.toSummary(post.getCreatedBy());
        var likeCount = redisCounterService.getPostLikeCount(post.getPostId());
        var commentCount = redisCounterService.getPostCommentCount(post.getPostId());
        PostRead postRead = PostRead.builder()
                .postId(post.getPostId())
                .content(post.getContent())
                .eventId(post.getEvent().getEventId())
                .createdById(creator.getUserId())
                .createdByUsername(creator.getUsername())
                .createdByFullName(creator.getFullName())
                .createdByAvatarId(creator.getAvatarId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(likeCount.orElse(0L))
                .commentCount(commentCount.orElse(0L))
                .build();
        PostRead saved = postReadRepository.save(postRead);
        if (likeCount.isEmpty()) {
            redisCounterService.setPostLikeCount(post.getPostId(), saved.getLikeCount());
        }
        if (commentCount.isEmpty()) {
            redisCounterService.setPostCommentCount(post.getPostId(), saved.getCommentCount());
        }
        redisCounterService.markPostDirty(post.getPostId());
        return saved;
    }

    public void updateContent(Post post) {
        PostRead postRead = postReadRepository.findById(post.getPostId()).orElse(null);
        if (postRead == null) {
            createFromPost(post);
            return;
        }

        postRead.setContent(post.getContent());
        postRead.setUpdatedAt(LocalDateTime.now());
        postReadRepository.save(postRead);
    }

    public void deleteByPostId(Long postId) {
        postReadRepository.deleteById(postId);
    }
}
