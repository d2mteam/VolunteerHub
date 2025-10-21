package com.volunteerhub.community.controller.query;

import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.repository.JpaRepository.EventRepository;
import lombok.AllArgsConstructor;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class EventResolver {
    private final EventRepository eventRepository;

    @QueryMapping
    public Event getEvent(@Argument long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }


}
