package com.volunteerhub.community.controller.graphql.mutation;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.service.write_service.IEventService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventMutation {
    private IEventService eventService;

    @MutationMapping
    public ActionResponse<Void> createEvent(@Argument CreateEventInput input) {
        return eventService.createEvent(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input); // placeholder for test
    }

    @MutationMapping
    public ActionResponse<Void> editEvent(@Argument EditEventInput input) {
        return eventService.editEvent(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), input);
    }

    @MutationMapping
    public ActionResponse<Void> deleteEvent(@Argument Long eventId) {
        return eventService.deleteEvent(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"), eventId);
    }
}
