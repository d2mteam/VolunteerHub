package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.request.CreateEventInput;
import com.volunteerhub.community.dto.rest.request.EditEventInput;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.service.write_service.IEventService;
import com.volunteerhub.community.service.write_service.IUserManagerService;
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
    public ResponseEntity<ModerationResponse> createEvent(@AuthenticationPrincipal UUID userId,
                                                          @Valid @RequestBody CreateEventInput input) {
        return ResponseEntity.ok(eventService.createEvent(userId, input));
    }

    @PutMapping
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> editEvent(@AuthenticationPrincipal UUID userId,
                                                        @Valid @RequestBody EditEventInput input) {
        return ResponseEntity.ok(eventService.editEvent(userId, input));
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> deleteEvent(@AuthenticationPrincipal UUID userId,
                                                          @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.deleteEvent(userId, eventId));
    }

    @PostMapping("/{eventId}/approve")
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<ModerationResponse> approveEvent(@AuthenticationPrincipal UUID userId,
                                                           @PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.approveEvent(eventId));
    }

    @PutMapping("/{eventId}/participants/{userId}/status")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> changeParticipationStatus(
            @PathVariable Long eventId,
            @PathVariable UUID userId,
            @RequestParam ParticipationStatus participationStatus
    ) {
        return ResponseEntity.ok(
                eventService.changeParticipationStatus(eventId, userId, participationStatus)
        );
    }
}
