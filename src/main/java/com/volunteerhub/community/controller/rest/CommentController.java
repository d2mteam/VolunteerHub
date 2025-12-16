package com.volunteerhub.community.controller.rest;

import com.volunteerhub.authentication.model.RolePermission;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;
import com.volunteerhub.community.dto.rest.request.CreateCommentInput;
import com.volunteerhub.community.dto.rest.request.EditCommentInput;
import com.volunteerhub.community.dto.rest.request.UpdateCommentRequest;
import com.volunteerhub.community.service.write_service.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PostMapping
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ModerationResponse> createComment(@AuthenticationPrincipal UUID userId,
                                                            @Valid @RequestBody CreateCommentInput input) {
        return ResponseEntity.ok(commentService.createComment(userId, input));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ModerationResponse> editComment(@AuthenticationPrincipal UUID userId,
                                                          @PathVariable Long commentId,
                                                          @Valid @RequestBody UpdateCommentRequest request) {
        EditCommentInput input = new EditCommentInput();
        input.setCommentId(commentId);
        input.setContent(request.getContent());
        return ResponseEntity.ok(commentService.editComment(userId, input));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize(RolePermission.USER)
    public ResponseEntity<ModerationResponse> deleteComment(@AuthenticationPrincipal UUID userId,
                                                            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(userId, commentId));
    }
}
