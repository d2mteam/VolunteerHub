package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.input.CreatePostInput;
import com.volunteerhub.community.dto.input.EditPostInput;
import com.volunteerhub.community.dto.output.Result;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PostMutationResolver {

    @MutationMapping
    public Result createPost(@Argument CreatePostInput input) {
        return null;
    }

    @MutationMapping
    public Result editPost(@Argument Long id, @Argument EditPostInput input) {
        return null;
    }

    @MutationMapping
    public Result deletePost(@Argument Long id) {
        return null;
    }
}
