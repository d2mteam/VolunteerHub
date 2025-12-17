package com.volunteerhub.community.controller.graphql.query;


import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
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

import org.dataloader.DataLoader;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class EventResolver {
    private final EventRepository eventRepository;
    private final PostRepository postRepository;

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

    @SchemaMapping(typeName = "Event", field = "listPosts")
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
    public CompletableFuture<Integer> likeCount(Event event,
                                               @org.springframework.graphql.data.method.annotation.DataLoader(name = "eventLikeCountLoader") DataLoader<Long, Integer> likeCountLoader) {
        return likeCountLoader.load(event.getEventId());
    }

    @SchemaMapping(typeName = "Event", field = "createBy")
    public CompletableFuture<UserProfile> createBy(Event event,
                                                   @org.springframework.graphql.data.method.annotation.DataLoader(name = "userProfileMiniLoader") DataLoader<UUID, UserProfile> userProfileLoader) {
        UUID creatorId = event.getCreatedBy() != null ? event.getCreatedBy().getUserId() : null;
        if (creatorId == null) {
            return CompletableFuture.completedFuture(null);
        }
        return userProfileLoader.load(creatorId);
    }
}
