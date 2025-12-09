package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.CountById;
import com.volunteerhub.community.dto.graphql.input.EventFilter;
import com.volunteerhub.community.dto.graphql.input.PostFilter;
import com.volunteerhub.community.dto.graphql.type.EventSummary;
import com.volunteerhub.community.dto.graphql.type.PostSummary;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.Post;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardResolverTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private EventRegistrationRepository eventRegistrationRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private DashboardResolver dashboardResolver;

    @Test
    void dashboardEventsUsesTrendingAndLimitAndSince() {
        EventFilter filter = new EventFilter();
        filter.setTrending(true);
        filter.setLimit(1);
        filter.setSince("2024-01-01T00:00:00");

        Event event = Event.builder()
                .eventId(1L)
                .eventName("Trending")
                .createdAt(LocalDateTime.parse("2024-02-01T00:00:00"))
                .createdBy(UserProfile.builder()
                        .userId(UUID.randomUUID())
                        .userName("owner")
                        .avatarId("avatar")
                        .build())
                .build();

        when(eventRepository.findTrendingEvents(any(), any()))
                .thenReturn(List.of(event));
        when(eventRegistrationRepository.countByEventIds(List.of(1L)))
                .thenReturn(List.of(new CountById(1L, 5L)));
        when(postRepository.countPostsByEventIds(List.of(1L)))
                .thenReturn(List.of(new CountById(1L, 2L)));
        when(likeRepository.countByTargetIdsAndType(List.of(1L), TableType.EVENT))
                .thenReturn(List.of(new CountById(1L, 7L)));

        List<EventSummary> results = dashboardResolver.dashboardEvents(filter);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findTrendingEvents(eq(LocalDateTime.parse("2024-01-01T00:00:00")), pageableCaptor.capture());

        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(1);
        assertThat(results).hasSize(1);
        EventSummary summary = results.getFirst();
        assertThat(summary.getEventName()).isEqualTo("Trending");
        assertThat(summary.getMemberCount()).isEqualTo(5);
        assertThat(summary.getPostCount()).isEqualTo(2);
        assertThat(summary.getLikeCount()).isEqualTo(7);
        assertThat(summary.getCreatorInfo().getUserName()).isEqualTo("owner");
    }

    @Test
    void dashboardEventsFallsBackToRecentWithDefaultLimit() {
        EventFilter filter = new EventFilter();
        filter.setRecentlyCreated(true);

        Event event = Event.builder()
                .eventId(2L)
                .eventName("Recent")
                .createdAt(LocalDateTime.parse("2024-03-01T00:00:00"))
                .build();

        when(eventRepository.findRecentEvents(null, any())).thenReturn(List.of(event));
        when(eventRegistrationRepository.countByEventIds(List.of(2L)))
                .thenReturn(List.of(new CountById(2L, 1L)));
        when(postRepository.countPostsByEventIds(List.of(2L)))
                .thenReturn(List.of(new CountById(2L, 0L)));
        when(likeRepository.countByTargetIdsAndType(List.of(2L), TableType.EVENT))
                .thenReturn(List.of());

        List<EventSummary> results = dashboardResolver.dashboardEvents(filter);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(eventRepository).findRecentEvents(null, pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getLikeCount()).isZero();
    }

    @Test
    void dashboardPostsFiltersByEventAndRespectsLimitAndOrder() {
        PostFilter filter = new PostFilter();
        filter.setEventIds(List.of(10L));
        filter.setLimit(2);
        filter.setRecent(false);

        Post first = Post.builder()
                .postId(1L)
                .eventId(10L)
                .createdAt(LocalDateTime.parse("2024-01-01T00:00:00"))
                .build();
        Post second = Post.builder()
                .postId(2L)
                .eventId(10L)
                .createdAt(LocalDateTime.parse("2024-02-01T00:00:00"))
                .build();

        when(postRepository.findOldestPosts(eq(List.of(10L)), any()))
                .thenReturn(List.of(first, second));
        when(commentRepository.countByPostIds(List.of(1L, 2L)))
                .thenReturn(List.of(new CountById(1L, 3L), new CountById(2L, 4L)));
        when(likeRepository.countByTargetIdsAndType(List.of(1L, 2L), TableType.POST))
                .thenReturn(List.of(new CountById(2L, 6L)));

        List<PostSummary> results = dashboardResolver.dashboardPosts(filter);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(postRepository).findOldestPosts(eq(List.of(10L)), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(2);

        assertThat(results).hasSize(2);
        assertThat(results.getFirst().getPostId()).isEqualTo(1L);
        assertThat(results.get(1).getLikeCount()).isEqualTo(6);
        assertThat(results.get(1).getCommentCount()).isEqualTo(4);
    }
}
