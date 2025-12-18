package com.volunteerhub.community.dto.dto;

import com.volunteerhub.community.model.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardTrendingEvent {
    private Event event;
    private long newMemberCount;
    private long newCommentCount;
    private long newLikeCount;
    private LocalDateTime latestInteractionAt;
}
