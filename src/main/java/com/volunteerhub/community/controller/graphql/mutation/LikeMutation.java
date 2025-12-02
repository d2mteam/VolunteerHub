package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.LikeInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.ILikeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class LikeMutation {
    private final ILikeService likeService;

    @MutationMapping
    public ActionResponse<Void> like(@AuthenticationPrincipal UUID userId,
                                     @Argument LikeInput input) {
        return likeService.like(userId,
                input.getTargetId(),
                input.getTargetType());
    }

    @MutationMapping
    public ActionResponse<Void> unlike(@AuthenticationPrincipal UUID userId,
                                       @Argument LikeInput input) {
        return likeService.unlike(userId,
                input.getTargetId(),
                input.getTargetType());
    }
}
