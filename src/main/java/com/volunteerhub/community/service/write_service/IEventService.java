package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.List;
import java.util.UUID;

public interface IEventService {
    ActionResponse<Void> createEvent(UUID userId, CreateEventInput input);
    ActionResponse<Void> editEvent(UUID userId, EditEventInput input);
    ActionResponse<Void> deleteEvent(UUID userId, Long eventId);
}
