package com.volunteerhub.community.dto.graphql.type;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventSummary {
    private Long eventId;
    private String eventName;
    private LocalDateTime createdAt;
    private Integer memberCount;
    private Integer postCount;
    private Integer likeCount;
    private UserProfileMini creatorInfo;
}
