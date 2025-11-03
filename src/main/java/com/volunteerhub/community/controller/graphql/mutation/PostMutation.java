package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.IPostService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PostMutation {
    private final IPostService postService;

    @MutationMapping
    public ActionResponse<Void> createPost(@Argument CreatePostInput input) {
        return postService.createPost(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input);
    }

    @MutationMapping
    public ActionResponse<Void> editPost(@Argument EditPostInput input) {
        return postService.editPost(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input);
    }

    @MutationMapping
    public ActionResponse<Void> deletePost(@Argument Long postId) {
        return postService.deletePost(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), postId);
    }
}
