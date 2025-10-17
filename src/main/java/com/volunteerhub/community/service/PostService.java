package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.CreatePostInput;
import com.volunteerhub.community.dto.input.EditPostInput;
import com.volunteerhub.community.dto.output.PostDto;

public interface PostService {
    PostDto createPost(CreatePostInput input);
    PostDto editPost(EditPostInput input);
    void deletePost(Long id);
}
