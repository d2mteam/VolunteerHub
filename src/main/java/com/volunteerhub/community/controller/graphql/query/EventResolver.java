package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.mv.EventDetail;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import com.volunteerhub.community.model.mv.PostDetail;
import com.volunteerhub.community.repository.mv.EventDetailRepository;
import com.volunteerhub.community.repository.mv.PostDetailRepository;
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
    private final EventDetailRepository eventDetailRepository;
    private final PostDetailRepository postDetailRepository;

    @QueryMapping
    public EventDetail getEvent(@Argument Long eventId) {
        return eventDetailRepository.findById(eventId).orElse(null);
    }

    @SchemaMapping(typeName = "Event", field = "listPosts")
    public OffsetPage<PostDetail> listPosts(EventDetail eventDetail,
                                            @Argument Integer page,
                                            @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<PostDetail> postPage = postDetailRepository.findByEventId(eventDetail.getEventId(), pageable);
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<PostDetail>builder()
                .content(postPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }


    @QueryMapping
    public OffsetPage<EventDetail> findEvents(@Argument Integer page,
                                                @Argument Integer size,
                                                @Argument Map<String, Object> filter) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<EventDetail> eventPage = eventDetailRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(eventPage);

        return OffsetPage.<EventDetail>builder()
                .content(eventPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Event", field = "memberCount")
    public Integer memberCount(EventDetail EventDetail) {
        return -1;
    }

    @SchemaMapping(typeName = "Event", field = "postCount")
    public Integer postCount(EventDetail EventDetail) {
        return -1;
    }

    @SchemaMapping(typeName = "Event", field = "likeCount")
    public Integer likeCount(EventDetail EventDetail) {
        return -1;
    }
}
