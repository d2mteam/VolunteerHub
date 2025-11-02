package com.volunteerhub.community.dto.graphql.output;

import java.time.OffsetDateTime;

public record PostDto(
        Long postId,
        Long eventId,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String creatorId,
        String creatorUsername,
        String creatorAvatar,
        String creatorRole,
        Long likeCount,
        Long commentCount
) {
    public UserSummaryDto createBy() {
        return new UserSummaryDto(creatorId, creatorUsername, creatorAvatar, creatorRole);
    }
}