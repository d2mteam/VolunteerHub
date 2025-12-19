package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.service.write_service.IUserManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final IUserManagerService userManagerService;

    @PostMapping("/{userId}/ban")
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<ModerationResponse> banUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userManagerService.banUser(userId));
    }

    @DeleteMapping("/{userId}/ban")
    @PreAuthorize(RolePermission.ADMIN)
    public ResponseEntity<ModerationResponse> unbanUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userManagerService.unbanUser(userId));
    }
}
