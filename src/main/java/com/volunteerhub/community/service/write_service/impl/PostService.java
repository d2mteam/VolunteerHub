package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.rest.request.CreatePostInput;
import com.volunteerhub.community.dto.rest.request.EditPostInput;
import com.volunteerhub.community.dto.rest.response.ModerationAction;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.response.ModerationResult;
import com.volunteerhub.community.dto.rest.response.ModerationStatus;
import com.volunteerhub.community.dto.rest.response.ModerationTargetType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.readmodel.PostReadService;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
import com.volunteerhub.community.service.write_service.IPostService;
import com.volunteerhub.media.model.MediaRefType;
import com.volunteerhub.media.service.MediaService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;
    private final EventRepository eventRepository;
    private final PostReadService postReadService;
    private final RedisCounterService redisCounterService;
    private final SnowflakeIdGenerator idGenerator;
    private final MediaService mediaService;

    @Override
    public ModerationResponse createPost(UUID userId, CreatePostInput input) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(input.getEventId());

        Post post = Post.builder()
                .postId(idGenerator.nextId())
                .content(input.getContent())
                .event(event)
                .createdBy(userProfile)
                .build();

        postRepository.save(post);
        mediaService.syncMediaResources(userId, MediaRefType.POST, post.getPostId(), input.getMediaIds());
        postReadService.createFromPost(post);
        redisCounterService.incrementEventPostCount(event.getEventId(), 1);
        redisCounterService.updateEventLatestPostAt(event.getEventId(), post.getCreatedAt());
        redisCounterService.updateEventLatestInteractionAt(event.getEventId(), post.getCreatedAt());

        return ModerationResponse.success(
                ModerationAction.CREATE_POST,
                ModerationTargetType.POST,
                post.getPostId().toString(),
                ModerationStatus.CREATED,
                "Post created"
        );
    }

    @Override
    public ModerationResponse editPost(UUID userId, EditPostInput input) {
        Optional<Post> optional = postRepository.findById(input.getPostId());
        if (optional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.EDIT_POST,
                    ModerationTargetType.POST,
                    input.getPostId().toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "Post not found",
                    "POST_NOT_FOUND"
            );
        }

        Post post = optional.get();
        post.setContent(input.getContent());
        postRepository.save(post);
        mediaService.syncMediaResources(userId, MediaRefType.POST, post.getPostId(), input.getMediaIds());
        postReadService.updateContent(post);

        return ModerationResponse.success(
                ModerationAction.EDIT_POST,
                ModerationTargetType.POST,
                post.getPostId().toString(),
                ModerationStatus.UPDATED,
                "Post updated"
        );
    }

    @Override
    public ModerationResponse deletePost(UUID userId, Long postId) {
        Optional<Post> optional = postRepository.findById(postId);
        if (optional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.DELETE_POST,
                    ModerationTargetType.POST,
                    postId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "Post not found",
                    "POST_NOT_FOUND"
            );
        }

        Post post = optional.get();
        postRepository.deleteById(postId);
        postReadService.deleteByPostId(postId);
        redisCounterService.incrementEventPostCount(post.getEvent().getEventId(), -1);
        redisCounterService.updateEventLatestInteractionAt(post.getEvent().getEventId(), LocalDateTime.now());

        return ModerationResponse.success(
                ModerationAction.DELETE_POST,
                ModerationTargetType.POST,
                postId.toString(),
                ModerationStatus.DELETED,
                "Post deleted"
        );
    }
}
