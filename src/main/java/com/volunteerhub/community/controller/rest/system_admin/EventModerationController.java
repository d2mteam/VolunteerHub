package com.volunteerhub.community.controller.rest.system_admin;

import com.volunteerhub.community.dto.rest.request.RejectEventRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/system-admin/event")
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
            @PathVariable Long eventId,
            @RequestBody RejectEventRequest request
    ) {
        return null;
    }
}
