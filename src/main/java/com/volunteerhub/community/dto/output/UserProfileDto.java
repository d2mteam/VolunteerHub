package com.volunteerhub.community.dto.output;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserProfileDto {
    private UUID userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
//    private String role;
}
