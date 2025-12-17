package com.volunteerhub.community.repository.projection;

import java.time.LocalDateTime;

public interface PostDashboardProjection {
    Long getPostId();
    Long getEventId();
    LocalDateTime getCreatedAt();
    Long getCommentCount();
    Long getLikeCount();
}
