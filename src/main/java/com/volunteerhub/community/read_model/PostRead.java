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
@RedisHash("post_read")
public class PostRead {
    @Id
    private UUID id;

    @Indexed
    private String content;

    @Indexed
    private Integer likeCount;

    private UserSummary createdBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;
}
