package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.IPostService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileService;
    private final EventRepository eventRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public void createPost(UUID userId, CreatePostInput input) {
        UserProfile userProfile = userProfileService.getReferenceById(userId);
        Event event = eventRepository.getReferenceById(input.getEventId());
        postRepository.save(Post.builder()
                .postId(idGenerator.nextId())
                .content(input.getContent())
                .event(event)
                .createdBy(userProfile)
                .build());
    }

    @Override
    public void editPost(UUID userId, EditPostInput input) {
        postRepository.findById(idGenerator.nextId()).ifPresent(post -> {
            post.setContent(input.getContent());
        });
    }

    @Override
    public void deletePost(UUID userId, Long postId) {
        postRepository.deleteById(postId);
    }
}
