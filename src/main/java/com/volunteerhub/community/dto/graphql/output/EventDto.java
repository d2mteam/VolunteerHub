package com.volunteerhub.community.dto.graphql.output;

import java.time.OffsetDateTime;

public record EventDto(
        Long eventId,
        String eventName,
        String eventDescription,
        String eventLocation,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String creatorId,
        String creatorUsername,
        String creatorAvatar,
        String creatorRole,
        Long likeCount,
        Long postCount,
        Long memberCount
) {
    public UserSummaryDto createBy() {
        return new UserSummaryDto(creatorId, creatorUsername, creatorAvatar, creatorRole);
    }
}