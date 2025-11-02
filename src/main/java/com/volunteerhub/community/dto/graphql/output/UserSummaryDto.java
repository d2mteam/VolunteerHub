package com.volunteerhub.community.dto.graphql.output;

public record UserSummaryDto(
        String userId,
        String username,
        String userAvatar,
        String role
) {
}