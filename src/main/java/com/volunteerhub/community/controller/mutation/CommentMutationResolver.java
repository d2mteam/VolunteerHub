package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.input.CreateCommentInput;
import com.volunteerhub.community.dto.input.EditCommentInput;
import com.volunteerhub.community.dto.output.Result;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentMutationResolver {

    @MutationMapping
    public Result createComment(@Argument CreateCommentInput input) {
        return null;
    }

    @MutationMapping
    public Result editComment(@Argument Long id, @Argument EditCommentInput input) {
        return null;
    }

    @MutationMapping
    public Result deleteComment(@Argument Long id) {
        return null;
    }
}