package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EditUserProfileInput {
    private String email;
    private String fullName;
    private String avatarUrl;
}
