package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EditPostInput {
    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content max 500 characters")
    private String content;

    private List<UUID> mediaIds;
}
