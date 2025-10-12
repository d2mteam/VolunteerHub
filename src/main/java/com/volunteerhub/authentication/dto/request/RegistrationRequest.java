package com.volunteerhub.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegistrationRequest {
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]{2,15}$", message = "Username must start with a letter and contain 3-16 letters, numbers, or underscores")
    private String username;

    @NotEmpty(message = "Password must not be empty")
    private String password;
    // ...
}
