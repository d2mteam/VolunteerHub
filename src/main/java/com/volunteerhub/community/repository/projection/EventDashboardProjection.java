package com.volunteerhub.community.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventDashboardProjection {
    Long getEventId();
    String getEventName();
    LocalDateTime getCreatedAt();
    Long getMemberCount();
    Long getPostCount();
    Long getLikeCount();
    UUID getCreatorId();
}
