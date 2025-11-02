package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;

import java.util.List;
import java.util.UUID;

public interface IEventService {
    void moderate(UUID userId, List<Long> eventIds);

    void createEvent(UUID userId, CreateEventInput input);

    void editEvent(UUID userId, EditEventInput input);

    void deleteEvent(UUID userId, Long eventId);
}
