package com.volunteerhub.community.controller.rest.system_admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/event")
public class EventModerationController {
    @GetMapping("/pending")
    public Object getPendingEvents() {
        return null;
    }


    @GetMapping("/{eventId}")
    public Object getEventDetail(@PathVariable Long eventId) {
        return null;
    }


    @PostMapping("/{eventId}/approve")
    public Object approveEvent(@PathVariable Long eventId) {
        return null;
    }


    @PostMapping("/{eventId}/reject")
    public Object rejectEvent(
            @PathVariable Long eventId
    ) {
        return null;
    }
}
