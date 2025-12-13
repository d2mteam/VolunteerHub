package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.dto.ModerationResponse;

import java.util.UUID;

public interface IEventRegistrationService {
    ModerationResponse approveRegistration(Long registrationId);
    ModerationResponse rejectRegistration(Long registrationId);

    ActionResponse<Void> registerEvent(UUID userId, Long eventId);
    ActionResponse<Void> unregisterEvent(UUID userId, Long eventId);
}