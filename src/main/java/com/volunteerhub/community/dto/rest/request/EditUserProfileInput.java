package com.volunteerhub.community.dto.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditUserProfileInput {
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 50, message = "Full name max 50 characters")
    private String fullName;

    @NotBlank(message = "username cannot be empty")
    @Size(max = 50, message = "Full name max 50 characters")
    private String username;

    private String avatarId;

    @Size(max = 255, message = "Bio max 255 characters")
    private String bio;


}
