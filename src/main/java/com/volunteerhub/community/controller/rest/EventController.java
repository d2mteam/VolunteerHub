package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.graphql.input.CreateEventInput;
import com.volunteerhub.community.dto.graphql.input.EditEventInput;
import com.volunteerhub.community.dto.rest.request.UpdateEventRequest;
import com.volunteerhub.community.service.write_service.IEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final IEventService eventService;

    @PostMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ActionResponse<Void>> createEvent(@AuthenticationPrincipal UUID userId,
                                                            @Valid @RequestBody CreateEventInput input) {
        return ResponseEntity.ok(eventService.createEvent(userId, input));
    }

    @PutMapping("/{eventId}")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ActionResponse<Void>> editEvent(@AuthenticationPrincipal UUID userId,
                                                          @PathVariable Long eventId,
                                                          @Valid @RequestBody UpdateEventRequest request) {
        EditEventInput input = EditEventInput.builder()
                .eventId(eventId)
                .eventName(request.getEventName())
                .eventDescription(request.getEventDescription())
                .eventLocation(request.getEventLocation())
                .eventDate(request.getEventDate())
                .build();
        return ResponseEntity.ok(eventService.editEvent(userId, input));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ActionResponse<Void>> deleteEvent(@AuthenticationPrincipal UUID userId,
                                                            @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.deleteEvent(userId, eventId));
    }

    @PostMapping("/{eventId}/approve")
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<ModerationResponse> approveEvent(@AuthenticationPrincipal UUID userId,
                                                           @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.approveEvent(eventId));
    }
}
