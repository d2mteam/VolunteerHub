package com.volunteerhub.community.dto.output;

import com.volunteerhub.community.entity.UserProfile;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserProfile createdBy;
}
