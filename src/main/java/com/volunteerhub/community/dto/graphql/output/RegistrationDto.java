package com.volunteerhub.community.dto.graphql.output;

import java.util.Map;
import java.util.UUID;

public record RegistrationDto(
        Long registrationId,
        UUID userId,
        String username,
        String fullName,
        String approvalStatus,
        Map<String, Object> extraInfos) {
}
