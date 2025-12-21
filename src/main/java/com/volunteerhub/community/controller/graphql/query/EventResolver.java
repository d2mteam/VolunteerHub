package com.volunteerhub.community.controller.graphql.query;


import com.volunteerhub.community.dto.graphql.input.EventFilterInput;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.readmodel.EventReadModel;
import com.volunteerhub.community.readmodel.PostReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.service.readmodel.EventReadModelService;
import com.volunteerhub.community.service.readmodel.PostReadModelService;
import com.volunteerhub.configuration.security.permission.HasPermission;
import com.volunteerhub.configuration.security.permission.PermissionAction;
import com.volunteerhub.ultis.page.OffsetPage;

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
    private final RoleInEventRepository roleInEventRepository;
    private final PostReadModelService postReadModelService;
    private final EventReadModelService eventReadModelService;

    @QueryMapping
    @HasPermission(action = PermissionAction.GET_EVENT, eventId = "#eventId")
    public EventReadModel getEvent(@Argument Long eventId) {
        return eventReadModelService.getEvent(eventId);
    }

    @QueryMapping
    public OffsetPage<EventReadModel> findEvents(@Argument Integer page,
                                                 @Argument Integer size,
                                                 @Argument Map<String, Object> filter) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Event> eventPage = eventRepository.findAll(pageable);
        return eventReadModelService.findEvents(eventPage);
    }

    //findEventsByEventManager
    @QueryMapping
    public OffsetPage<EventReadModel> findEventsByEventManager(@AuthenticationPrincipal UUID userId,
                                                               @Argument Integer page,
                                                               @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Event> eventPage = eventRepository.findByCreatedBy_UserId(userId, pageable);
        return eventReadModelService.findByManager(eventPage);
    }

    @SchemaMapping(typeName = "Event", field = "listPost")
    public OffsetPage<PostReadModel> listPosts(EventReadModel event,
                                               @Argument Integer page,
                                               @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        return postReadModelService.listByEvent(event.getEventId(), pageable);
    }

    @SchemaMapping(typeName = "Event", field = "memberCount")
    public Integer memberCount(EventReadModel event) {
        EventReadModel cached = eventReadModelService.getEvent(event.getEventId());
        return cached != null ? cached.getMemberCount() : 0;
    }

    @SchemaMapping(typeName = "Event", field = "postCount")
    public Integer postCount(EventReadModel event) {
        EventReadModel cached = eventReadModelService.getEvent(event.getEventId());
        return cached != null ? cached.getPostCount() : 0;
    }

    @SchemaMapping(typeName = "Event", field = "categories")
    public List<String> categories(EventReadModel event) {
        EventReadModel cached = eventReadModelService.getEvent(event.getEventId());
        return cached != null ? cached.getCategories() : Collections.emptyList();
    }

    @SchemaMapping(typeName = "Event", field = "likeCount")
    public Integer likeCount(EventReadModel event) {
        EventReadModel cached = eventReadModelService.getEvent(event.getEventId());
        return cached != null ? cached.getLikeCount() : 0;
    }

    @SchemaMapping(typeName = "Event", field = "createBy")
    public UserProfileSummaryView createBy(EventReadModel event) {
        EventReadModel cached = eventReadModelService.getEvent(event.getEventId());
        return cached != null ? cached.getCreatedBy() : null;
    }


    @QueryMapping
    public List<EventReadModel> searchEvents(@Argument EventFilterInput filter) {
        List<Event> events = eventRepository.searchEvents(filter.getKeyword(),
                filter.getLocation(),
                filter.getCategories().toArray(String[]::new),
                filter.getStartDateFrom(),
                filter.getStartDateTo(),
                filter.getEventState());
        return eventReadModelService.mapToReadModels(events);
    }

    @SchemaMapping(typeName = "Event", field = "isJoined")
    public boolean isJoined(@AuthenticationPrincipal UUID userId, EventReadModel event) {
        return roleInEventRepository.existsByUserProfile_UserIdAndEvent_EventIdAndParticipationStatusNotIn(userId, event.getEventId(),
                List.of(ParticipationStatus.CANCELLED,
                        ParticipationStatus.LEFT_EVENT,
                        ParticipationStatus.REJECTED,
                        ParticipationStatus.PENDING));
    }
}
