package com.volunteerhub.community.service.user_service;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.UUID;

public interface IEventRegistrationService {
    ActionResponse<Void> registerEvent(UUID userId, Long eventId);
    ActionResponse<Void> unregisterEvent(UUID userId, Long eventId);
}
