package com.volunteerhub.community.dto.graphql.type;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileMini {
    private UUID userId;
    private String userName;
    private String avatarId;
}
