package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.graphql.input.CreatePostInput;
import com.volunteerhub.community.dto.graphql.input.EditPostInput;
import com.volunteerhub.community.service.write_service.IPostService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);


    private final IPostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@AuthenticationPrincipal UUID userId,
                                           @RequestBody CreatePostInput input) {
        postService.createPost(userId, input);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editPost(@AuthenticationPrincipal UUID userId,
                                         @PathVariable Long id,
                                         @RequestBody EditPostInput input) {
        postService.editPost(userId, input);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal UUID userId,
                                           @PathVariable Long id) {

        postService.deletePost(userId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
