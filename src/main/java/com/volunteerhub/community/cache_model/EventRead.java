package com.volunteerhub.community.cache_model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@RedisHash("event_read")
public class Event {
    @Id
    private String eventId;

    @Indexed
    private String eventName;

    private String eventDescription;
    private String eventLocation;
    private UserProfile createdBy;
    private Integer likeCount;
    private Integer memberCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
