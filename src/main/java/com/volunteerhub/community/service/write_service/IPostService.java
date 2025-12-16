package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.CreatePostInput;
import com.volunteerhub.community.dto.rest.request.EditPostInput;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IPostService {
    ModerationResponse createPost(UUID userId, CreatePostInput input);
    ModerationResponse editPost(UUID userId, EditPostInput input);
    ModerationResponse deletePost(UUID userId, Long postId);
}
