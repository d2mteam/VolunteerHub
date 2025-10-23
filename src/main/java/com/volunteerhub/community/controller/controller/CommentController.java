package com.volunteerhub.community.controller.controller;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.service.write_service.ICommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@AuthenticationPrincipal UUID userId,
                                              @RequestBody CreateCommentInput input) {
        commentService.createComment(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    @PutMapping
    public ResponseEntity<Void> editComment(@AuthenticationPrincipal UUID userId,
                                            @RequestBody EditCommentInput input) {
        commentService.editComment(userId, input);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal UUID userId,
                                              @PathVariable Long id) {
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
