package com.volunteerhub.community.service.user_service;

import com.volunteerhub.community.dto.graphql.input.RegistrationInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.graphql.output.RegistrationDto;
import com.volunteerhub.community.dto.graphql.page.OffsetPage;

import java.util.Map;
import java.util.UUID;

public interface IRegistrationService {
    ActionResponse<Void> registerToEvent(UUID userId, RegistrationInput input);
    ActionResponse<Void> cancelRegistration(UUID userId, int registrationId);
    ActionResponse<Void> updateRegistration(UUID userId, int registrationId, Map<String, Object> extraInfos);
    ActionResponse<OffsetPage<RegistrationDto>> getRegistrationsByUserId(UUID userId, int page, int size);
}
