package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardTrendingEvent {
    private EventSummaryView event;
    private long newMemberCount;
    private long newCommentCount;
    private long newLikeCount;
    private LocalDateTime latestInteractionAt;
}
