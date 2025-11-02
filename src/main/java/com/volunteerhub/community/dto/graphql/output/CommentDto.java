package com.volunteerhub.community.dto.graphql.output;

import java.time.OffsetDateTime;

public record CommentDto(
        Long commentId,
        Long postId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String creatorId,
        String creatorUsername,
        String creatorAvatar,
        String creatorRole,
        Long likeCount
) {
    public UserSummaryDto createBy() {
        return new UserSummaryDto(creatorId, creatorUsername, creatorAvatar, creatorRole);
    }
}