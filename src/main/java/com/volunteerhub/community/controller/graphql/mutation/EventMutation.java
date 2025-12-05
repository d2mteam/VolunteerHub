package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.service.write_service.IEventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventMutation {
    private IEventService eventService;

    @MutationMapping
    @PreAuthorize(RolePermission.ADMIN)
    public ActionResponse<Void> approveEvent(@AuthenticationPrincipal UUID userId,
                                             @Argument Long eventId) {
        return eventService.approveEvent(eventId);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ActionResponse<Void> createEvent(@AuthenticationPrincipal UUID userId,
                                            @Valid @Argument CreateEventInput input) {
        return eventService.createEvent(userId, input);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ActionResponse<Void> editEvent(@AuthenticationPrincipal UUID userId,
                                          @Valid @Argument EditEventInput input) {
        return eventService.editEvent(userId, input);
    }

    @MutationMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ActionResponse<Void> deleteEvent(@AuthenticationPrincipal UUID userId,
                                            @Valid @Argument Long eventId) {
        return eventService.deleteEvent(userId, eventId);
    }
}
