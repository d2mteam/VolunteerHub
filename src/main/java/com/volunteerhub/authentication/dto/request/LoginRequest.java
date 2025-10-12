package com.volunteerhub.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {
    @NotEmpty(message = "Username must be not null")
    private String username;

    @NotEmpty(message = "Password must be not null")
    private String password;
}
