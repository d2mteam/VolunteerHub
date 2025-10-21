package com.volunteerhub.community.read_model;

import lombok.Data;

import java.util.UUID;

@Data
public class EventSummary {
    private UUID id;

    private String eventName;

    private String eventDescription;

    private String eventLocation;
}
