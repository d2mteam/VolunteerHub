package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.dto.ModerationResponse;

import java.util.UUID;

public interface IEventService {
    ModerationResponse approveEvent(Long eventId);
    ActionResponse<Void> createEvent(UUID userId, CreateEventInput input);
    ActionResponse<Void> editEvent(UUID userId, EditEventInput input);
    ActionResponse<Void> deleteEvent(UUID userId, Long eventId);
}
