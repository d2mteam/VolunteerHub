package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.Post;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.model.permission.Permission;
import com.volunteerhub.community.model.permission.ResourceNode;
import com.volunteerhub.community.model.permission.ResourceType;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.permission.PermissionGraphService;
import com.volunteerhub.community.service.write_service.IPostService;
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
    private final PermissionGraphService permissionGraphService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ActionResponse<Void> createPost(UUID userId, CreatePostInput input) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(input.getEventId());
        permissionGraphService.assertPermission(userId, ResourceType.EVENT, event.getEventId(), Permission.POST);

        Post post = Post.builder()
                .postId(idGenerator.nextId())
                .content(input.getContent())
                .event(event)
                .createdBy(userProfile)
                .build();

        postRepository.save(post);

        ResourceNode eventNode = permissionGraphService.getNode(ResourceType.EVENT, event.getEventId());
        permissionGraphService.registerChildResource(ResourceType.POST, post.getPostId(), userId, eventNode, Permission.MODERATE);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                post.getPostId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> editPost(UUID userId, EditPostInput input) {
        Optional<Post> optional = postRepository.findById(input.getPostId());
        if (optional.isEmpty()) {
            return ActionResponse.failure("Post not found");
        }

        Post post = optional.get();
        permissionGraphService.assertPermission(userId, ResourceType.POST, post.getPostId(), Permission.MODERATE);
        post.setContent(input.getContent());
        postRepository.save(post);

        return ActionResponse.success(
                post.getPostId().toString(),
                post.getCreatedAt(),
                LocalDateTime.now()
        );
    }

    @Override
    public ActionResponse<Void> deletePost(UUID userId, Long postId) {
        boolean exists = postRepository.existsById(postId);
        if (!exists) {
            return ActionResponse.failure("Post not found");
        }

        permissionGraphService.assertPermission(userId, ResourceType.POST, postId, Permission.MODERATE);
        permissionGraphService.softDelete(ResourceType.POST, postId);
        postRepository.deleteById(postId);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                postId.toString(),
                now,
                now
        );
    }
}
