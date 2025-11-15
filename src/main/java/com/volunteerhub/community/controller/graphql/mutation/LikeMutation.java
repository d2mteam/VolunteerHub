package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.LikeInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.ILikeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class LikeMutation {
    private final ILikeService likeService;

    @MutationMapping
    public ActionResponse<Void> like(@Argument LikeInput input) {
        return likeService.like(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                input.getTargetId(),
                input.getTargetType());
    }

    @MutationMapping
    public ActionResponse<Void> unlike(@Argument LikeInput input) {
        return likeService.unlike(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                input.getTargetId(),
                input.getTargetType());
    }
}
