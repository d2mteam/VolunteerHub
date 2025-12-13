package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequest {
    @NotNull(message = "Target ID is required")
    private Long targetId;

    @NotBlank(message = "Target type is required")
    private String targetType;
}
