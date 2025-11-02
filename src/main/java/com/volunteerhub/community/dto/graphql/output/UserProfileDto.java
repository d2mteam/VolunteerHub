package com.volunteerhub.community.dto.graphql.output;


import java.time.OffsetDateTime;

public record UserProfileDto(
        String userId,
        String username,
        String userAvatar,
        String email,
        String role,
        String status,
        OffsetDateTime createdAt,
        Long postCount,
        Long commentCount,
        Long eventCount
) {
}