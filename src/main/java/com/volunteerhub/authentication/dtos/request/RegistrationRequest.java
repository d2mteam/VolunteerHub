package com.volunteerhub.authentication.dtos.request;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String username;
    private String password;
    // ...
}
