package com.volunteerhub.community.controller.rest.event_manager;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event-manager/events/{eventId}/member")
public class EventMemberManagementController {
    @PatchMapping("/{userId}/complete")
    public ResponseEntity<?> completeEvent(@PathVariable Long eventId, @PathVariable UUID userId) {
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<?> getParticipants(@PathVariable Long eventId) {
        return ResponseEntity.ok().build();
    }
}