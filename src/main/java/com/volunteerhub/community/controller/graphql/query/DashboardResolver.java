package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.controller.graphql.mapper.EventSummaryMapper;
import com.volunteerhub.community.controller.graphql.mapper.PostSummaryMapper;
import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.dto.graphql.input.EventFilter;
import com.volunteerhub.community.dto.graphql.input.PostFilter;
import com.volunteerhub.community.dto.graphql.type.EventSummary;
import com.volunteerhub.community.dto.graphql.type.PostSummary;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.Post;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class DashboardResolver {
    private static final int DEFAULT_LIMIT = 10;

    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @QueryMapping
    public List<EventSummary> dashboardEvents(@Argument EventFilter filter) {
        EventFilter safeFilter = Objects.requireNonNullElseGet(filter, EventFilter::new);
        int limit = resolveLimit(safeFilter.getLimit());
        LocalDateTime since = parseSince(safeFilter.getSince());

        Pageable pageable = PageRequest.of(0, limit);
        List<Event> events = Boolean.TRUE.equals(safeFilter.getTrending())
                ? eventRepository.findTrendingEvents(since, pageable)
                : eventRepository.findRecentEvents(since, pageable);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getEventId)
                .toList();

        Map<Long, Long> memberCounts = toCountMap(eventRegistrationRepository.countByEventIds(eventIds));
        Map<Long, Long> postCounts = toCountMap(postRepository.countPostsByEventIds(eventIds));
        Map<Long, Long> likeCounts = toCountMap(likeRepository.countByTargetIdsAndType(eventIds, TableType.EVENT));

        return events.stream()
                .map(event -> EventSummaryMapper.toSummary(event, memberCounts, postCounts, likeCounts))
                .toList();
    }

    @QueryMapping
    public List<PostSummary> dashboardPosts(@Argument PostFilter filter) {
        PostFilter safeFilter = Objects.requireNonNullElseGet(filter, PostFilter::new);
        int limit = resolveLimit(safeFilter.getLimit());
        Pageable pageable = PageRequest.of(0, limit);

        List<Long> eventIds = safeFilter.getEventIds();
        if (eventIds != null && eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Post> posts = Boolean.FALSE.equals(safeFilter.getRecent())
                ? postRepository.findOldestPosts(eventIds, pageable)
                : postRepository.findRecentPosts(eventIds, pageable);

        if (posts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getPostId)
                .toList();

        Map<Long, Long> commentCounts = toCountMap(commentRepository.countByPostIds(postIds));
        Map<Long, Long> likeCounts = toCountMap(likeRepository.countByTargetIdsAndType(postIds, TableType.POST));

        return posts.stream()
                .map(post -> PostSummaryMapper.toSummary(post, commentCounts, likeCounts))
                .toList();
    }

    private int resolveLimit(Integer limit) {
        return (limit != null && limit > 0) ? limit : DEFAULT_LIMIT;
    }

    private LocalDateTime parseSince(String since) {
        if (since == null || since.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(since, DateTimeFormatter.ISO_DATE_TIME);
    }

    private Map<Long, Long> toCountMap(List<CountById> counters) {
        return counters.stream()
                .collect(Collectors.toMap(CountById::id, CountById::count));
    }
}
