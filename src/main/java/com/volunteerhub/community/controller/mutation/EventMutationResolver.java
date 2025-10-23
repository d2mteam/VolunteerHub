package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.input.CreateEventInput;
import com.volunteerhub.community.dto.input.EditEventInput;
import com.volunteerhub.community.dto.output.Result;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class EventMutationResolver {

    @MutationMapping
    public Result createEvent(@Argument CreateEventInput input) {
        return null;
    }

    @MutationMapping
    public Result editEvent(@Argument Long eventId, @Argument EditEventInput input) {
        return null;
    }

    @MutationMapping
    public Result deleteEvent(@Argument Long eventId) {
        return null;
    }
}
