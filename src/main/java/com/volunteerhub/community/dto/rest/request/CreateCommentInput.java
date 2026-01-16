package com.volunteerhub.community.dto.rest.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateCommentInput {
    private Long postId;
    private String content;
    private List<UUID> mediaIds;
}
