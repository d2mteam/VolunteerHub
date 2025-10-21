package com.volunteerhub.community.read_model;

import com.redis.om.spring.annotations.Document;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@Data
@Builder
@Document
@RedisHash("comment_read")
public class CommentRead {
    @Id
    private UUID id;

    @Indexed
    private String content;

    private UserSummary createdBy;

    @Indexed
    private Integer likeCount;
}
