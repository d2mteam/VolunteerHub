package com.volunteerhub.authentication.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpRequest {
    private String email;
//    private String username;
//    private String fullName;
    private String password;
}
