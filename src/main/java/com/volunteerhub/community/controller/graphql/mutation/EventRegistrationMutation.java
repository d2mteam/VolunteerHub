package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.user_service.IEventRegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventRegistrationMutation {
    private final IEventRegistrationService eventRegistrationService;

    @MutationMapping
    public ActionResponse<Void> registerEvent(@Argument Long eventId) {
        return eventRegistrationService
                .registerEvent(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), eventId);
    }

    @MutationMapping
    public ActionResponse<Void> unregisterEvent(@Argument Long eventId) {
        return eventRegistrationService
                .unregisterEvent(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), eventId);
    }
}
