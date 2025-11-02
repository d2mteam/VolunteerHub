package com.volunteerhub.community.controller.graphql;

import com.volunteerhub.community.dto.graphql.page.OffsetPage;
import com.volunteerhub.community.dto.graphql.page.PageInfo;
import com.volunteerhub.community.dto.graphql.page.PageUtils;
import com.volunteerhub.community.entity.mv.EventDetail;
import com.volunteerhub.community.entity.mv.PostDetail;
import com.volunteerhub.community.repository.mv.EventDetailRepository;
import com.volunteerhub.community.repository.mv.PostDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

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
    public OffsetPage<PostDetail> listPosts(EventDetail eventDetail, @Argument Integer page, @Argument Integer size) {
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
}
