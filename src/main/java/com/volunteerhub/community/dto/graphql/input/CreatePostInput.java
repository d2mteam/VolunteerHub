package com.volunteerhub.community.dto.graphql.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePostInput {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content max 500 characters")
    private String content;
}
