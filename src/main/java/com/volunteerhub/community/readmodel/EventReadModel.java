package com.volunteerhub.community.readmodel;

import com.redis.om.spring.annotations.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("event-read")
public class EventReadModel implements Serializable {
    @Id
    private Long eventId;

    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String eventState;
    @Builder.Default
    private List<String> categories = new ArrayList<>();

    private Integer memberCount;
    private Integer postCount;
    private Integer likeCount;
    private UserProfileSummaryView createdBy;

    @Builder.Default
    @TimeToLive
    private Duration ttl = Duration.ofMinutes(10);
}
