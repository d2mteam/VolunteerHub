package com.volunteerhub.community.controller.rest.management;

import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.dto.rest.request.UpdateRoleRequest;
import com.volunteerhub.community.entity.db_enum.SystemRole;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/manager/users")
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

    @PostMapping("/{userId}/system-role")
    public ResponseEntity<ActionResponse<Void>> updateSystemRole(@PathVariable UUID userId,
                                                                 @RequestBody UpdateRoleRequest newSystemRole) {
        return ResponseEntity.ok(ActionResponse.success("", LocalDateTime.now(), LocalDateTime.now()));
    }
}
