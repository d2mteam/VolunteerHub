package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.request.EditUserProfile;
import com.volunteerhub.community.service.write_service.IUserProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user-profiles")
public class UserProfileController {
    private final IUserProfileService userProfileService;

    @PostMapping()
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<?> createUserProfile(@AuthenticationPrincipal UUID userId,
                                               @Valid @RequestBody EditUserProfile editUserProfile) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userId, editUserProfile));
    }


    @PutMapping()
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<?> editUserProfile(@AuthenticationPrincipal UUID userId,
                                             @Valid @RequestBody EditUserProfile editUserProfile) {
        return ResponseEntity.ok(userProfileService.editUserProfile(userId, editUserProfile));
    }
}
