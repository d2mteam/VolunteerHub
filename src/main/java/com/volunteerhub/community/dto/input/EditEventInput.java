package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditEventInput {
    private String eventName;
    private String eventDescription;
    private String eventLocation;
}
