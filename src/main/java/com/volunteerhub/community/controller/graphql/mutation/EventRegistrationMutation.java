package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.ActionResponse;


import com.volunteerhub.community.service.write_service.IEventRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventRegistrationMutation {

    private final IEventRegistrationService eventRegistrationService;

    @MutationMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ActionResponse<Void> approveRegistration(@Argument Long registrationId) {
        return eventRegistrationService.approveRegistration(registrationId);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ActionResponse<Void> rejectRegistration(@Argument Long registrationId) {
        return eventRegistrationService.rejectRegistration(registrationId);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.USER)
    public ActionResponse<Void> registerEvent(@AuthenticationPrincipal UUID userId,
                                              @Argument Long eventId) {
        return eventRegistrationService.registerEvent(userId, eventId);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.USER)
    public ActionResponse<Void> unregisterEvent(@AuthenticationPrincipal UUID userId,
                                                @Argument Long eventId) {
        return eventRegistrationService.unregisterEvent(userId, eventId);
    }
}
