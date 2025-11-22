package com.volunteerhub.community.controller.rest.event_manager;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.page.OffsetPage;
import com.volunteerhub.community.model.table.EventRegistration;
import com.volunteerhub.community.service.manager_service.IEventRegistrationManagerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event-manager/events/{eventId}/registrations")
public class RegistrationManagementController {
    
    private final IEventRegistrationManagerService service;

    @GetMapping
    public ResponseEntity<OffsetPage<EventRegistration>> listRegistrations(@PathVariable Long eventId) {
        return null;
    }

    @GetMapping("/pending")
    public ResponseEntity<OffsetPage<EventRegistration>> listPendingRegistrations(@PathVariable Long eventId) {
        return null;
    }
    
    @PostMapping("/{registrationId}/approve")
    public ResponseEntity<ActionResponse<Void>> approveRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId
    ) {
        return ResponseEntity.ok(service.approveRegistration(registrationId));
    }


    @PostMapping("/{registrationId}/reject")
    public Object rejectRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId
    ) {
        return ResponseEntity.ok(service.rejectRegistration(registrationId));
    }
}
