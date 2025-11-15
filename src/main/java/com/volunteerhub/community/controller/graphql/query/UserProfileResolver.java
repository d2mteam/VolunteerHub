package com.volunteerhub.community.controller.graphql.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.volunteerhub.community.dto.page.OffsetPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.mv.EventDetail;
import com.volunteerhub.community.entity.mv.UserProfileDetail;
import com.volunteerhub.community.repository.mv.EventDetailRepository;
import com.volunteerhub.community.repository.mv.UserProfileDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserProfileResolver {
    private final UserProfileDetailRepository userProfileDetailRepository;
    private final EventDetailRepository eventDetailRepository;

    @QueryMapping
    public UserProfileDetail getUserProfile(@Argument UUID userId) {
        return userProfileDetailRepository.findById(userId).orElse(null);
    }

    @SchemaMapping(typeName = "UserProfile", field = "listEvents")
    public OffsetPage<EventDetail> listEvents(UserProfileDetail userProfileDetail,
                                              @Argument Integer page, @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<EventDetail> eventPage = eventDetailRepository.findAllByUserId(userProfileDetail.getUserId(), pageable);
        PageInfo pageInfo = PageUtils.from(eventPage);
        return OffsetPage.<EventDetail>builder()
                .content(eventPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }


    @QueryMapping
    public OffsetPage<UserProfileDetail> findUserProfiles(@Argument Integer page, @Argument Integer size,
                                              @Argument JsonNode filter) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<UserProfileDetail> userProfilePage = userProfileDetailRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(userProfilePage);
        return OffsetPage.<UserProfileDetail>builder()
                .content(userProfilePage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

}
