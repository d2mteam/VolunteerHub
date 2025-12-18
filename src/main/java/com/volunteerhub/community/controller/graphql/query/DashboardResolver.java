package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.graphql.response.DashboardEventActivity;
import com.volunteerhub.community.dto.graphql.response.DashboardOverview;
import com.volunteerhub.community.dto.graphql.response.DashboardTrendingEvent;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.view.EventEngagementSummary;
import com.volunteerhub.community.repository.view.EventPostActivitySummary;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class DashboardResolver {
    private final EventRepository eventRepository;
    private final PostRepository postRepository;

    @QueryMapping
    public DashboardOverview dashboardOverview(@Argument Integer hours,
                                               @Argument Integer size) {
        int safeSize = size != null && size > 0 ? size : 5;
        int safeHours = hours != null && hours > 0 ? hours : 24;
        LocalDateTime since = LocalDateTime.now().minusHours(safeHours);

        PageRequest pageRequest = PageRequest.of(0, safeSize);

        List<Event> recentEvents = eventRepository.findByOrderByCreatedAtDesc(pageRequest).getContent();

        List<DashboardEventActivity> postActivities = postRepository.findEventPostActivitySince(since, pageRequest)
                .stream()
                .map(this::toDashboardEventActivity)
                .collect(Collectors.toList());

        List<DashboardTrendingEvent> trending = eventRepository.findTrendingEventsSince(since, TableType.EVENT, pageRequest)
                .stream()
                .map(this::toTrendingEvent)
                .collect(Collectors.toList());

        return DashboardOverview.builder()
                .newlyPublished(recentEvents)
                .recentWithNewPosts(postActivities)
                .trending(trending)
                .build();
    }

    private DashboardEventActivity toDashboardEventActivity(EventPostActivitySummary summary) {
        return DashboardEventActivity.builder()
                .event(summary.getEvent())
                .newPostCount(summary.getNewPostCount())
                .latestPostAt(summary.getLatestPostAt())
                .build();
    }

    private DashboardTrendingEvent toTrendingEvent(EventEngagementSummary summary) {
        return DashboardTrendingEvent.builder()
                .event(summary.getEvent())
                .newMemberCount(summary.getNewMemberCount())
                .newCommentCount(summary.getNewCommentCount())
                .newLikeCount(summary.getNewLikeCount())
                .latestInteractionAt(summary.getLatestInteractionAt())
                .build();
    }
}
