package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;

import java.util.UUID;

public interface IPostService {
    void createPost(UUID userId, CreatePostInput input);
    void editPost(UUID userId, EditPostInput input);
    void deletePost(UUID userId, Long postId);
}
