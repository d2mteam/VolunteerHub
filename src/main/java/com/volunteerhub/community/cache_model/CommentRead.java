package com.volunteerhub.community.cache_model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("comment_read")
public class CommentRead {
    @Id
    private String id;

    private String content;

    private UserSummary created_by;
}
