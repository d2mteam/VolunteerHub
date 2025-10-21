package com.volunteerhub.community.read_model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import com.redis.om.spring.annotations.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document
@RedisHash("event_read")
public class EventRead {
    @Id
    private UUID id;

    @Indexed
    private String eventName;

    @Indexed
    private String eventDescription;

    @Indexed
    private String eventLocation;

    @Indexed
    private Integer likeCount;

    @Indexed
    private Integer memberCount;

    private UserSummary createdBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;


}
