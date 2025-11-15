package com.volunteerhub.community.controller.rest.management;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/manager/events/{eventId}/registrations")
public class RegistrationManagementController {

    @GetMapping
    public Object listRegistrations(@PathVariable Long eventId) {
        return null;
    }

    @GetMapping("/pending")
    public Object listPendingRegistrations(@PathVariable Long eventId) {
        return null;
    }

    @GetMapping("/{registrationId}")
    public Object getRegistrationDetail(
            @PathVariable Long eventId,
            @PathVariable Long registrationId
    ) {
        return null;
    }


    @PostMapping("/{registrationId}/approve")
    public Object approveRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId
    ) {
        return null;
    }


    @PostMapping("/{registrationId}/reject")
    public Object rejectRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId,
            @RequestBody(required = false) String reason
    ) {
        return null;
    }
}
