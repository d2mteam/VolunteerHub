package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.CreatePostInput;
import com.volunteerhub.community.dto.output.PostDto;

public interface PostService {
    PostDto createPost(CreatePostInput input);
    void deletePost(Long id);
}
