package com.volunteerhub.community.controller.mutation;

import com.volunteerhub.community.dto.input.CreateCommentInput;
import com.volunteerhub.community.service.write_service.ICommentService;
import com.volunteerhub.ultis.CustomPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@AllArgsConstructor
public class CommentMutationResolver {

    private final ICommentService commentService;

    @MutationMapping
    @PreAuthorize("hasRole('USER') && hasRole('EVENT_MANAGER')")
    void createComment(@AuthenticationPrincipal CustomPrincipal principal, @Argument CreateCommentInput input) {

    }

    @MutationMapping
    @PreAuthorize("hasRole('USER') && hasRole('EVENT_MANAGER')")
    void editComment(@AuthenticationPrincipal CustomPrincipal principal, @Argument Long id, @Argument CreateCommentInput input) {
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    void deleteComment(@AuthenticationPrincipal CustomPrincipal principal, @Argument Long id) {
    }

}
