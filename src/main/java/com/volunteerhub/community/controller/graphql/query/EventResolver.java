package com.volunteerhub.community.controller.graphql.query;


import com.volunteerhub.community.dto.graphql.input.EventFilterInput;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.entity.RoleInEvent;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.*;
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
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class EventResolver {
    private final EventRepository eventRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;
    private final LikeRepository likeRepository;
    private final RoleInEventRepository roleInEventRepository;

    @QueryMapping
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

    @SchemaMapping(typeName = "Event", field = "listPost")
    public OffsetPage<Post> listPosts(Event event,
                                      @Argument Integer page,
                                      @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Post> postPage = postRepository.findByEvent_EventId(event.getEventId(), pageable);
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<Post>builder()
                .content(postPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Event", field = "likeCount")
    public Integer likeCount(Event event) {
        return likeRepository.countByTargetIdAndTableType(event.getEventId(), TableType.EVENT);
    }

    @SchemaMapping(typeName = "Event", field = "createBy")
    public UserProfile createBy(Event event) {
        return userProfileRepository.findById(event.getCreatedBy().getUserId()).orElse(null);
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
}
