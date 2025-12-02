package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.service.write_service.IEventService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventMutation {
    private IEventService eventService;

    @MutationMapping
    public ActionResponse<Void> createEvent(@AuthenticationPrincipal UUID userId,
                                            @Argument CreateEventInput input) {
        return eventService.createEvent(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> editEvent(@AuthenticationPrincipal UUID userId,
                                          @Argument EditEventInput input) {
        return eventService.editEvent(userId, input);
    }

    @MutationMapping
    public ActionResponse<Void> deleteEvent(@AuthenticationPrincipal UUID userId,
                                            @Argument Long eventId) {
        return eventService.deleteEvent(userId, eventId);
    }
}
