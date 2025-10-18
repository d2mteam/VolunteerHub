package com.volunteerhub.community.cache_model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@RedisHash("post_read")
public class Post {
    @Id
    private String id;

    private String content;
    private List<Comment> comments;

    private Integer likeCount;
    private UserSummary createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
