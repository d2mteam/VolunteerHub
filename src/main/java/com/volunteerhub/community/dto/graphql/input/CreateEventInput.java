package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreateEventInput {
    private String eventName;
    private String eventDescription;
    private String eventLocation;
}
