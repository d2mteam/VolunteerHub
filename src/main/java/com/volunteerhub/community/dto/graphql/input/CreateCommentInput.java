package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentInput {
    private Long postId;
    private String content;
}
