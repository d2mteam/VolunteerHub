package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EditUserProfileInput {
    private String username;
    private String email;
}
