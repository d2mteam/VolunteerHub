package com.volunteerhub.community.dto.graphql.output;

import com.volunteerhub.community.model.entity.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DashboardEventActivity {
    private Event event;
    private long newPostCount;
    private LocalDateTime latestPostAt;
}
