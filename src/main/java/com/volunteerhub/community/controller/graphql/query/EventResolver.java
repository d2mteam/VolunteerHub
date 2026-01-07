package com.volunteerhub.community.controller.graphql.query;


import com.volunteerhub.community.dto.graphql.input.EventFilterInput;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.*;
import com.volunteerhub.community.dto.graphql.output.UserProfileSummaryView;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import com.volunteerhub.configuration.security.permission.HasPermission;
import com.volunteerhub.configuration.security.permission.PermissionAction;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@AllArgsConstructor
public class EventResolver {
    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final PostReadRepository postReadRepository;
    private final RoleInEventRepository roleInEventRepository;
    private final LikeRepository likeRepository;
    private final RedisCounterService redisCounterService;
    private final UserProfileCacheService userProfileCacheService;

    @QueryMapping
    @HasPermission(action = PermissionAction.GET_EVENT, eventId = "#eventId")
    public Event getEvent(@Argument Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    @QueryMapping
    public OffsetPage<Event> findEvents(@Argument Integer page,
                                        @Argument Integer size,
                                        @Argument Map<String, Object> filter) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Event> eventPage = eventRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(eventPage);

        return OffsetPage.<Event>builder()
                .content(eventPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    //findEventsByEventManager
    @QueryMapping
    public OffsetPage<Event> findEventsByEventManager(@AuthenticationPrincipal UUID userId,
                                                      @Argument Integer page,
                                                      @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Event> eventPage = eventRepository.findByCreatedBy_UserId(userId, pageable);
        PageInfo pageInfo = PageUtils.from(eventPage);

        return OffsetPage.<Event>builder()
                .content(eventPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Event", field = "listPost")
    public OffsetPage<PostRead> listPosts(Event event,
                                      @Argument Integer page,
                                      @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<PostRead> postPage = postReadRepository.findByEventId(event.getEventId(), pageable);
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<PostRead>builder()
                .content(postPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Event", field = "memberCount")
    public Integer memberCount(Event event) {
        return redisCounterService.getEventMemberCount(event.getEventId())
                .map(Long::intValue)
                .orElseGet(() -> {
                    long count = roleInEventRepository.countByEvent(event.getEventId());
                    redisCounterService.setEventMemberCount(event.getEventId(), count);
                    return Math.toIntExact(count);
                });
    }

    @SchemaMapping(typeName = "Event", field = "postCount")
    public Integer postCount(Event event) {
        return redisCounterService.getEventPostCount(event.getEventId())
                .map(Long::intValue)
                .orElseGet(() -> {
                    long count = postRepository.countByEvent(event.getEventId());
                    redisCounterService.setEventPostCount(event.getEventId(), count);
                    return Math.toIntExact(count);
                });
    }

    @SchemaMapping(typeName = "Event", field = "categories")
    public List<String> categories(Event event) {
        Map<String, Object> metadata = event.getMetadata();
        if (metadata == null) {
            return Collections.emptyList();
        }

        Object categories = metadata.getOrDefault("categories", Collections.emptyList());
        if (categories instanceof List<?>) {
            return ((List<?>) categories).stream()
                    .map(Object::toString)
                    .toList();
        }

        return Collections.emptyList();
    }

    @SchemaMapping(typeName = "Event", field = "likeCount")
    public Integer likeCount(Event event) {
        return redisCounterService.getEventLikeCount(event.getEventId())
                .map(Long::intValue)
                .orElseGet(() -> {
                    int count = likeRepository.countByTargetIdAndTableType(event.getEventId(), TableType.EVENT);
                    redisCounterService.setEventLikeCount(event.getEventId(), count);
                    return count;
                });
    }

    @SchemaMapping(typeName = "Event", field = "createBy")
    public UserProfileSummaryView createBy(Event event) {
        return userProfileCacheService.getSummary(event.getCreatedBy().getUserId());
    }


    @QueryMapping
    public List<Event> searchEvents(@Argument EventFilterInput filter) {
        return eventRepository.searchEvents(filter.getKeyword(),
                filter.getLocation(),
                filter.getCategories().toArray(String[]::new),
                filter.getStartDateFrom(),
                filter.getStartDateTo(),
                filter.getEventState());
    }

    @SchemaMapping(typeName = "Event", field = "isJoined")
    public boolean isJoined(@AuthenticationPrincipal UUID userId, Event event) {
        return roleInEventRepository.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusNotIn(userId, event.getEventId(),
                List.of(ParticipationStatus.CANCELLED,
                        ParticipationStatus.LEFT_EVENT,
                        ParticipationStatus.REJECTED,
                        ParticipationStatus.PENDING));
    }
}
