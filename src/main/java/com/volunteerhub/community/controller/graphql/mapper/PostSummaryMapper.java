package com.volunteerhub.community.controller.graphql.mapper;

import com.volunteerhub.community.dto.graphql.type.PostSummary;
import com.volunteerhub.community.model.Post;

import java.util.Map;

public final class PostSummaryMapper {
    private PostSummaryMapper() {
    }

    public static PostSummary toSummary(Post post,
                                        Map<Long, Long> commentCounts,
                                        Map<Long, Long> likeCounts) {
        return PostSummary.builder()
                .postId(post.getPostId())
                .eventId(post.getEventId())
                .createdAt(post.getCreatedAt())
                .commentCount(extractCount(commentCounts, post.getPostId()))
                .likeCount(extractCount(likeCounts, post.getPostId()))
                .build();
    }

    private static int extractCount(Map<Long, Long> counts, Long id) {
        return counts.getOrDefault(id, 0L).intValue();
    }
}
