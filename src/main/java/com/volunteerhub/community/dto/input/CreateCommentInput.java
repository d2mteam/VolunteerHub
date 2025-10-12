package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateCommentInput {
    private String content;
    private UUID authorId;
    private UUID createBy;
}
