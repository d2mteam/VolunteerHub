package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.CreateEventInput;
import com.volunteerhub.community.dto.rest.request.EditEventInput;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;

import java.util.UUID;

public interface IEventService {
    ModerationResponse approveEvent(Long eventId);

    ModerationResponse createEvent(UUID userId, CreateEventInput input);

    ModerationResponse editEvent(UUID userId, EditEventInput input);

    ModerationResponse deleteEvent(UUID userId, Long eventId);

    ModerationResponse changeParticipationStatus(Long eventId, UUID userId, ParticipationStatus participationStatus);
}
