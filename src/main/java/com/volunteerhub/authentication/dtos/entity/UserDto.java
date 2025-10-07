package com.volunteerhub.authentication.dtos.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String username;
    private String hashPassword;
}
