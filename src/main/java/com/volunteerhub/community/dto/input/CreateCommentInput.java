package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCommentInput {
    private Long postId;
    private String content;
}
