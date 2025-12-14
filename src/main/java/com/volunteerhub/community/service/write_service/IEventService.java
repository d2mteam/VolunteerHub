package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.ModerationResponse;

import java.util.UUID;

public interface IEventService {
    ModerationResponse approveEvent(Long eventId);
    ModerationResponse createEvent(UUID userId, CreateEventInput input);
    ModerationResponse editEvent(UUID userId, EditEventInput input);
    ModerationResponse deleteEvent(UUID userId, Long eventId);
}
