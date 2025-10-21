package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.input.CreatePostInput;
import com.volunteerhub.community.dto.input.EditPostInput;

import java.util.UUID;

public interface IPostWriteService {
    void createPost(UUID userID, CreatePostInput input);
    void editPost(UUID userID, EditPostInput input);
    void deletePostById(UUID userID, Long id);

}
