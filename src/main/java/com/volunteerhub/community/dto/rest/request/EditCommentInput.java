package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditCommentInput {

    @NotNull(message = "Comment ID is required")
    private Long commentId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content max 500 characters")
    private String content;
}
