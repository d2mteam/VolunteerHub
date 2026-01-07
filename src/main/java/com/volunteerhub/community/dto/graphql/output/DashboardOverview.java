package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardOverview {
    private List<EventSummaryView> newlyPublished;
    private List<DashboardEventActivity> recentWithNewPosts;
    private List<DashboardTrendingEvent> trending;
}
