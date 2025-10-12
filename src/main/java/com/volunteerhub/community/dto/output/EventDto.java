package com.volunteerhub.community.dto.output;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventDto {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
}
