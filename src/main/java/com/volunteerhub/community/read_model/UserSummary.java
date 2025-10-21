package com.volunteerhub.community.read_model;

import lombok.Data;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
public class UserSummary {
    @Indexed
    private UUID id;

    @Indexed
    private String username;

    @Indexed
    private String avatar;

    @Indexed
    private String role;
}
