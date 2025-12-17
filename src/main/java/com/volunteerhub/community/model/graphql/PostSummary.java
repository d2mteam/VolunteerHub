package com.volunteerhub.community.model.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostSummary {
    private final Long postId;
    private final Long eventId;
    private final LocalDateTime createdAt;
    private final Integer commentCount;
    private final Integer likeCount;
}
