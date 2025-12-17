package com.volunteerhub.community.controller.graphql.dto;

import com.volunteerhub.community.model.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardOverview {
    private List<Event> newlyPublished;
    private List<DashboardEventActivity> recentWithNewPosts;
    private List<DashboardTrendingEvent> trending;
}
