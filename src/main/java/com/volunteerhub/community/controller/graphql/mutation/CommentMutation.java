package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.service.write_service.ICommentService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class CommentMutation {
    private final ICommentService commentService;

    @MutationMapping
    public ActionResponse<Void> createComment(@AuthenticationPrincipal UUID userId,
                                              @Argument CreateCommentInput input) {
        return commentService.createComment(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> editComment(@AuthenticationPrincipal UUID userId,
                                            @Argument EditCommentInput input) {
        return commentService.editComment(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> deleteComment(@AuthenticationPrincipal UUID userId,
                                              @Argument Long commentId) {
        return commentService.deleteComment(userId, commentId);
    }

}
