package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.IPostService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PostMutation {
    private final IPostService postService;

    @MutationMapping
    public ActionResponse<Void> createPost(@AuthenticationPrincipal UUID userId,
                                           @Argument CreatePostInput input) {
        return postService.createPost(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> editPost(@AuthenticationPrincipal UUID userId,
                                         @Argument EditPostInput input) {
        return postService.editPost(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> deletePost(@AuthenticationPrincipal UUID userId,
                                           @Argument Long postId) {
        return postService.deletePost(userId, postId);
    }
}
