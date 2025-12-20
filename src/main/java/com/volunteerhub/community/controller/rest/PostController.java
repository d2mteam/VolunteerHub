package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.request.CreatePostInput;
import com.volunteerhub.community.dto.rest.request.EditPostInput;
import com.volunteerhub.community.service.write_service.IPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    @PostMapping
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> createPost(@AuthenticationPrincipal UUID userId,
                                                         @Valid @RequestBody CreatePostInput input) {
        return ResponseEntity.ok(postService.createPost(userId, input));
    }

    @PutMapping
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> editPost(@AuthenticationPrincipal UUID userId,
                                                       @Valid @RequestBody EditPostInput input) {
        return ResponseEntity.ok(postService.editPost(userId, input));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize(RolePermission.USER_OR_EVENT_MANAGER)
    public ResponseEntity<ModerationResponse> deletePost(@AuthenticationPrincipal UUID userId,
                                                         @PathVariable Long postId) {
        return ResponseEntity.ok(postService.deletePost(userId, postId));
    }
}
