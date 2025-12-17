package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.model.graphql.EventFilterInput;
import com.volunteerhub.community.model.graphql.EventSummary;
import com.volunteerhub.community.model.graphql.PostFilterInput;
import com.volunteerhub.community.model.graphql.PostSummary;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.projection.EventDashboardProjection;
import com.volunteerhub.community.repository.projection.PostDashboardProjection;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class DashboardResolver {
    private static final int DEFAULT_LIMIT = 5;

    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;

    @QueryMapping
    public List<EventSummary> dashboardEvents(@Argument EventFilterInput filter) {
        DashboardCriteria criteria = toEventCriteria(filter);
        List<EventDashboardProjection> projections = eventRepository.fetchDashboardEvents(
                criteria.recentOnly(),
                criteria.trending(),
                criteria.since(),
                criteria.limit()
        );

        Map<UUID, UserProfile> creators = loadCreators(projections);
        return projections.stream()
                .map(projection -> EventSummary.builder()
                        .eventId(projection.getEventId())
                        .eventName(projection.getEventName())
                        .createdAt(projection.getCreatedAt())
                        .memberCount(Math.toIntExact(projection.getMemberCount()))
                        .postCount(Math.toIntExact(projection.getPostCount()))
                        .likeCount(Math.toIntExact(projection.getLikeCount()))
                        .creatorInfo(creators.get(projection.getCreatorId()))
                        .build())
                .toList();
    }

    @QueryMapping
    public List<PostSummary> dashboardPosts(@Argument PostFilterInput filter) {
        DashboardCriteria criteria = toPostCriteria(filter);
        List<PostDashboardProjection> projections = postRepository.fetchDashboardPosts(
                criteria.recentOnly(),
                criteria.trending(),
                criteria.since(),
                criteria.limit(),
                criteria.hasEventFilter(),
                criteria.eventIds()
        );

        return projections.stream()
                .map(projection -> PostSummary.builder()
                        .postId(projection.getPostId())
                        .eventId(projection.getEventId())
                        .createdAt(projection.getCreatedAt())
                        .commentCount(Math.toIntExact(projection.getCommentCount()))
                        .likeCount(Math.toIntExact(projection.getLikeCount()))
                        .build())
                .toList();
    }

    private Map<UUID, UserProfile> loadCreators(List<EventDashboardProjection> projections) {
        Set<UUID> creatorIds = projections.stream()
                .map(EventDashboardProjection::getCreatorId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (creatorIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userProfileRepository.findAllById(creatorIds)
                .stream()
                .collect(Collectors.toMap(UserProfile::getUserId, Function.identity()));
    }

    private DashboardCriteria toEventCriteria(EventFilterInput filter) {
        int limit = filter != null && filter.getLimit() != null && filter.getLimit() > 0 ? filter.getLimit() : DEFAULT_LIMIT;
        boolean trending = filter != null && Boolean.TRUE.equals(filter.getTrending());
        boolean recentOnly = filter != null && Boolean.TRUE.equals(filter.getRecentlyCreated());
        LocalDateTime since = parseSince(filter != null ? filter.getSince() : null);
        return new DashboardCriteria(limit, trending, recentOnly, false, Collections.emptyList(), since);
    }

    private DashboardCriteria toPostCriteria(PostFilterInput filter) {
        int limit = filter != null && filter.getLimit() != null && filter.getLimit() > 0 ? filter.getLimit() : DEFAULT_LIMIT;
        boolean recentOnly = filter != null && Boolean.TRUE.equals(filter.getRecent());
        boolean trending = filter != null && Boolean.TRUE.equals(filter.getTrending());
        LocalDateTime since = parseSince(filter != null ? filter.getSince() : null);
        List<Long> eventIds = filter != null && filter.getEventIds() != null ? filter.getEventIds() : Collections.emptyList();
        boolean hasEventFilter = !eventIds.isEmpty();
        List<Long> normalizedEventIds = hasEventFilter ? eventIds : List.of(-1L);
        return new DashboardCriteria(limit, trending, recentOnly, hasEventFilter, normalizedEventIds, since);
    }

    private LocalDateTime parseSince(String since) {
        if (since == null || since.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(since);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private record DashboardCriteria(int limit,
                                     boolean trending,
                                     boolean recentOnly,
                                     boolean hasEventFilter,
                                     List<Long> eventIds,
                                     LocalDateTime since) {
    }
}
