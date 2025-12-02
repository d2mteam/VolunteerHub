package com.volunteerhub.community.controller.rest.system_admin;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/system-admin/users")
public class UserManagementController {
    @PostMapping("/{userId}/ban")
    public ResponseEntity<ActionResponse<Void>> banUser(@PathVariable UUID userId) {
        ActionResponse<Void> resp = ActionResponse.success(userId.toString(), LocalDateTime.now(), LocalDateTime.now());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{userId}/unban")
    public ResponseEntity<ActionResponse<Void>> unbanUser(@PathVariable UUID userId) {
        ActionResponse<Void> resp = ActionResponse.success(userId.toString(), LocalDateTime.now(), LocalDateTime.now());
        return ResponseEntity.ok(resp);
    }
}
