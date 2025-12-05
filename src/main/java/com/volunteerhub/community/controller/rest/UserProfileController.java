package com.volunteerhub.community.controller.rest;

import com.volunteerhub.community.dto.rest.request.EditUserProfile;
import com.volunteerhub.community.service.write_service.IUserProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user-profile")
public class UserProfileController {
    private final IUserProfileService userProfileService;

    @PostMapping()
    public ResponseEntity<?> createUserProfile(@AuthenticationPrincipal UUID userId,
                                               @Valid @RequestBody EditUserProfile editUserProfile) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userId, editUserProfile));
    }


    @PutMapping()
    public ResponseEntity<?> editUserProfile(@AuthenticationPrincipal UUID userId,
                                             @Valid @RequestBody EditUserProfile editUserProfile) {
        return ResponseEntity.ok(userProfileService.editUserProfile(userId, editUserProfile));
    }
}
