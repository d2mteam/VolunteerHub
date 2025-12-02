package com.volunteerhub.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S{8,}$",
            message = "Password must be at least 8 characters and contain no spaces")
    private String password;
}
