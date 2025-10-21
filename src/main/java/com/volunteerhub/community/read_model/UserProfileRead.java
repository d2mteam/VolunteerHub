package com.volunteerhub.community.read_model;

import com.redis.om.spring.annotations.Document;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document
@RedisHash("user_profile_read")
public class UserProfileRead {
    @Id
    private UUID id;

    @Indexed
    private String username;

    @Indexed
    private String email;

    @Indexed
    private String avatar;

    private List<String> roles;

    @Indexed
    private LocalDateTime createdAt;
}
