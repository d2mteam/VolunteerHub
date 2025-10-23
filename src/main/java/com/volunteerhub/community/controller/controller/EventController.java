package com.volunteerhub.community.controller.controller;

import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.service.write_service.IEventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final IEventService eventService;

    @PostMapping
    public ResponseEntity<Void> createEvent(@AuthenticationPrincipal UUID userId,
                                            @RequestBody CreateEventInput input) {
        eventService.createEvent(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> editEvent(@AuthenticationPrincipal UUID userId,
                            @RequestBody EditEventInput input) {
        eventService.editEvent(userId, input);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@AuthenticationPrincipal UUID userId,
                              @PathVariable Long eventId) {
        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.noContent().build();
    }
}
