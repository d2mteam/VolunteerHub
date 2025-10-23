package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditEventInput {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String eventLocation;
}
