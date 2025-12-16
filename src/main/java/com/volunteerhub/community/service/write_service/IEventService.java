package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.CreateEventInput;
import com.volunteerhub.community.dto.rest.request.EditEventInput;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IEventService {
    ModerationResponse approveEvent(Long eventId);
    ModerationResponse createEvent(UUID userId, CreateEventInput input);
    ModerationResponse editEvent(UUID userId, EditEventInput input);
    ModerationResponse deleteEvent(UUID userId, Long eventId);
}
