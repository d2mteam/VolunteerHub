package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardEventActivity {
    private EventSummaryView event;
    private long newPostCount;
    private LocalDateTime latestPostAt;
}
