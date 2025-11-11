package com.volunteerhub.community.service.management_service;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.graphql.output.RegistrationDto;
import com.volunteerhub.community.dto.graphql.page.OffsetPage;

public interface IRegistrationManagerService {
    ActionResponse<Void> approveRegistration(Long registrationId);
    ActionResponse<Void> rejectRegistration(Long registrationId, String reason);
    ActionResponse<OffsetPage<RegistrationDto>> findRegistrationsInEvent(long eventId, int page, int size);
}
