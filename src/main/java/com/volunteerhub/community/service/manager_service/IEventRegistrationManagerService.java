package com.volunteerhub.community.service.manager_service;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;

public interface IEventRegistrationManagerService {
    ActionResponse<Void> approveRegistration(Long registrationId);
    ActionResponse<Void> rejectRegistration(Long registrationId);
}
