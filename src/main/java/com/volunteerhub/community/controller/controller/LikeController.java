package com.volunteerhub.community.controller.controller;

import com.volunteerhub.community.dto.graphql.input.LikeInput;
import com.volunteerhub.community.service.write_service.ILikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final ILikeService likeService;

    @PostMapping
    public ResponseEntity<Void> like(@AuthenticationPrincipal UUID userId,
                                     @RequestBody LikeInput input) {
        likeService.like(userId, input.getTargetId(), input.getTargetType());
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unlike(@AuthenticationPrincipal UUID userId,
                                       @PathVariable Long targetId) {
        likeService.unLike(userId, targetId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
