package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostRequest {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content max 500 characters")
    private String content;
}
