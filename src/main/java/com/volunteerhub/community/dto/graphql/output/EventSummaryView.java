package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventSummaryView {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String eventState;
}
