package com.volunteerhub.media.controller;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.media.dto.request.ConfirmUploadRequest;
import com.volunteerhub.media.dto.request.UploadTicketRequest;
import com.volunteerhub.media.dto.response.ConfirmUploadResponse;
import com.volunteerhub.media.dto.response.DownloadUrlResponse;
import com.volunteerhub.media.dto.response.UploadTicketResponse;
import com.volunteerhub.media.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {
    private final MediaService mediaService;

    @PostMapping("/upload-ticket")
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<UploadTicketResponse> createUploadTicket(@AuthenticationPrincipal UUID userId,
                                                                   @Valid @RequestBody UploadTicketRequest request) {
        return ResponseEntity.ok(mediaService.createUploadTicket(userId, request));
    }

    @PostMapping("/confirm")
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<ConfirmUploadResponse> confirmUpload(@AuthenticationPrincipal UUID userId,
                                                               @Valid @RequestBody ConfirmUploadRequest request) {
        return ResponseEntity.ok(mediaService.confirmUpload(userId, request));
    }

    @GetMapping("/{resourceId}/download-url")
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<DownloadUrlResponse> getDownloadUrl(@AuthenticationPrincipal UUID userId,
                                                              @PathVariable UUID resourceId) {
        return ResponseEntity.ok(mediaService.getDownloadUrl(userId, resourceId));
    }
}
