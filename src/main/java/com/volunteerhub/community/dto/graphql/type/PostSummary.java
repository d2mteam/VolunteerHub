package com.volunteerhub.community.dto.graphql.type;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostSummary {
    private Long postId;
    private Long eventId;
    private LocalDateTime createdAt;
    private Integer commentCount;
    private Integer likeCount;
}
