package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.PostReadRepository;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
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

    public void createFromPost(Post post) {
        var creator = userProfileCacheService.toSummary(post.getCreatedBy());
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
                .likeCount(0)
                .commentCount(0)
                .build();
        postReadRepository.save(postRead);
        redisCounterService.setPostLikeCount(post.getPostId(), 0);
        redisCounterService.setPostCommentCount(post.getPostId(), 0);
        redisCounterService.markPostDirty(post.getPostId());
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
