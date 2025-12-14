package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.rest.request.LikeRequest;
import com.volunteerhub.community.service.write_service.ILikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final ILikeService likeService;

    @PostMapping
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ModerationResponse> like(@AuthenticationPrincipal UUID userId,
                                                   @Valid @RequestBody LikeRequest request) {
        return ResponseEntity.ok(likeService.like(userId, request.getTargetId(), request.getTargetType()));
    }

    @DeleteMapping
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ModerationResponse> unlike(@AuthenticationPrincipal UUID userId,
                                                     @Valid @RequestBody LikeRequest request) {
        return ResponseEntity.ok(likeService.unlike(userId, request.getTargetId(), request.getTargetType()));
    }
}
