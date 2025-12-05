package com.volunteerhub.community.dto.graphql.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeInput {
    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotNull(message = "TargetType is required")
    private String targetType;
}
