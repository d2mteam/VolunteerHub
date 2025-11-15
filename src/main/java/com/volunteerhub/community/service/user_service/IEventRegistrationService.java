package com.volunteerhub.community.service.user_service;

import com.volunteerhub.community.dto.graphql.input.RegistrationInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.UUID;

public interface IEventRegistrationService {
    ActionResponse<Void> registerEvent(UUID userId, RegistrationInput input);
    ActionResponse<Void> unregisterEvent(UUID userId, Long registrationId);
}
