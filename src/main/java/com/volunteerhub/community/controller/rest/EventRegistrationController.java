package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.ActionResponse;
import com.volunteerhub.community.service.write_service.IEventRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final IEventRegistrationService eventRegistrationService;

    @PostMapping("/events/{eventId}/registrations")
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ActionResponse<Void>> registerEvent(@AuthenticationPrincipal UUID userId,
                                                              @PathVariable Long eventId) {
        return ResponseEntity.ok(eventRegistrationService.registerEvent(userId, eventId));
    }

    @DeleteMapping("/events/{eventId}/registrations")
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ActionResponse<Void>> unregisterEvent(@AuthenticationPrincipal UUID userId,
                                                                @PathVariable Long eventId) {
        return ResponseEntity.ok(eventRegistrationService.unregisterEvent(userId, eventId));
    }

    @PostMapping("/event-registrations/{registrationId}/approve")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ActionResponse<Void>> approveRegistration(@PathVariable Long registrationId) {
        return ResponseEntity.ok(eventRegistrationService.approveRegistration(registrationId));
    }

    @PostMapping("/event-registrations/{registrationId}/reject")
    @PreAuthorize(RolePermission.EVENT_MANAGER)
    public ResponseEntity<ActionResponse<Void>> rejectRegistration(@PathVariable Long registrationId) {
        return ResponseEntity.ok(eventRegistrationService.rejectRegistration(registrationId));
    }
}
