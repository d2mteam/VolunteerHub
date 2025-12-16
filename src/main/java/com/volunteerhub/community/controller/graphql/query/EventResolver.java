package com.volunteerhub.community.controller.graphql.query;


import com.volunteerhub.community.configuration.graphql.BatchLoaderConfig;
import com.volunteerhub.community.model.Event;
import com.volunteerhub.community.model.Post;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;

import org.dataloader.DataLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;
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


    @SchemaMapping(typeName = "Event", field = "memberCount")
    public CompletableFuture<Integer> memberCount(Event event, DataFetchingEnvironment env) {
        DataLoader<Long, Integer> dataloader = env.getDataLoader("memberCountLoader");
        return dataloader.load(event.getEventId());
    }

    @SchemaMapping(typeName = "Event", field = "postCount")
    public CompletableFuture<Integer> postCount(Event event, DataFetchingEnvironment env) {
        DataLoader<Long, Integer> dataloader = env.getDataLoader("postCountLoader");
        return dataloader.load(event.getEventId());
    }

    @SchemaMapping(typeName = "Event", field = "likeCount")
    public CompletableFuture<Integer> likeCount(Event event, DataFetchingEnvironment env) {
        DataLoader<com.volunteerhub.community.service.cache.CounterKey, Integer> dataloader = env.getDataLoader("likeCountLoader");
        return dataloader.load(BatchLoaderConfig.likeKey(TableType.EVENT, event.getEventId()));
    }
}
