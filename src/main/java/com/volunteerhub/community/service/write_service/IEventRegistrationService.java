package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.ActionResponse;

import java.util.UUID;

public interface IEventRegistrationService {
    ActionResponse<Void> approveRegistration(Long registrationId);
    ActionResponse<Void> rejectRegistration(Long registrationId);

    ActionResponse<Void> registerEvent(UUID userId, Long eventId);
    ActionResponse<Void> unregisterEvent(UUID userId, Long eventId);
}