package com.volunteerhub.community.controller.graphql.query;

import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventRegistrationResolver {
    @QueryMapping
    public Map<String, Object> browseEvents(@Argument int page,
                                            @Argument int size,
                                            @Argument Map<String, Object> filter) {

        return Map.of();
    }

    @QueryMapping
    public Map<String, Object> getEventRegistrationByUserId(@Argument UUID userId) {
        return Map.of();
    }

    @QueryMapping
    public Map<String, Object> getEventRegistrationByEventId(@Argument Long eventId) {
        return Map.of();
    }
}
