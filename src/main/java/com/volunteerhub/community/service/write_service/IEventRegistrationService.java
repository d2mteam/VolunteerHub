package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface IEventRegistrationService {
    ModerationResponse approveRegistration(Long registrationId);
    ModerationResponse rejectRegistration(Long registrationId);

    ModerationResponse registerEvent(UUID userId, Long eventId);
    ModerationResponse unregisterEvent(UUID userId, Long eventId);
}