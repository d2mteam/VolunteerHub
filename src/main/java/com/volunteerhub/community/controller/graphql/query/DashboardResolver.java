package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.graphql.output.DashboardEventActivity;
import com.volunteerhub.community.dto.graphql.output.DashboardOverview;
import com.volunteerhub.community.dto.graphql.output.DashboardTrendingEvent;
import com.volunteerhub.community.dto.graphql.output.EventSummaryView;
import com.volunteerhub.community.model.read.EventActivitySummary;
import com.volunteerhub.community.repository.EventActivitySummaryRepository;
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
    private final EventActivitySummaryRepository eventActivitySummaryRepository;

    @QueryMapping
    public DashboardOverview dashboardOverview(@Argument Integer hours,
                                               @Argument Integer size) {
        int safeSize = size != null && size > 0 ? size : 5;
        int safeHours = hours != null && hours > 0 ? hours : 24;
        LocalDateTime since = LocalDateTime.now().minusHours(safeHours);

        PageRequest pageRequest = PageRequest.of(0, safeSize);

        List<EventSummaryView> recentEvents = eventActivitySummaryRepository.findByOrderByCreatedAtDesc(pageRequest)
                .stream()
                .map(this::toEventSummaryView)
                .collect(Collectors.toList());

        List<DashboardEventActivity> postActivities = eventActivitySummaryRepository
                .findByLatestPostAtAfterOrderByLatestPostAtDesc(since, pageRequest)
                .stream()
                .map(this::toDashboardEventActivity)
                .collect(Collectors.toList());

        List<DashboardTrendingEvent> trending = eventActivitySummaryRepository.findTrendingSince(since, pageRequest)
                .stream()
                .map(this::toTrendingEvent)
                .collect(Collectors.toList());

        return DashboardOverview.builder()
                .newlyPublished(recentEvents)
                .recentWithNewPosts(postActivities)
                .trending(trending)
                .build();
    }

    private DashboardEventActivity toDashboardEventActivity(EventActivitySummary summary) {
        return DashboardEventActivity.builder()
                .event(toEventSummaryView(summary))
                .newPostCount(summary.getNewPostCount())
                .latestPostAt(summary.getLatestPostAt())
                .build();
    }

    private DashboardTrendingEvent toTrendingEvent(EventActivitySummary summary) {
        return DashboardTrendingEvent.builder()
                .event(toEventSummaryView(summary))
                .newMemberCount(summary.getNewMemberCount())
                .newCommentCount(summary.getNewCommentCount())
                .newLikeCount(summary.getNewLikeCount())
                .latestInteractionAt(summary.getLatestInteractionAt())
                .build();
    }

    private EventSummaryView toEventSummaryView(EventActivitySummary summary) {
        return EventSummaryView.builder()
                .eventId(summary.getEventId())
                .eventName(summary.getEventName())
                .eventDescription(summary.getEventDescription())
                .eventLocation(summary.getEventLocation())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .eventState(summary.getEventState() == null ? null : summary.getEventState().name())
                .build();
    }
}
