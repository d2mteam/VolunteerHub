package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.ICommentService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class CommentMutation {
    private final ICommentService commentService;

    @MutationMapping
    public ActionResponse<Void> createComment(@Argument CreateCommentInput input) {
        return commentService.createComment(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input);
    }

    @MutationMapping
    public ActionResponse<Void> editComment(@Argument EditCommentInput input) {
        return commentService.editComment(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input);
    }

    @MutationMapping
    public ActionResponse<Void> deleteComment(@Argument Long commentId) {
        return commentService.deleteComment(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), commentId);
    }

}
