package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.UUID;

public interface IPostService {
    ActionResponse<Void> createPost(UUID userId, CreatePostInput input);
    ActionResponse<Void> editPost(UUID userId, EditPostInput input);
    ActionResponse<Void> deletePost(UUID userId, Long postId);
}
