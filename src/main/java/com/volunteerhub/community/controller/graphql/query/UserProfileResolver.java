package com.volunteerhub.community.controller.graphql.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import com.volunteerhub.community.model.mv.EventDetail;
import com.volunteerhub.community.model.mv.UserProfileDetail;
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

    @QueryMapping
    public UserProfileDetail getUserProfile(@Argument UUID userId) {
        return userProfileDetailRepository.findById(userId).orElse(null);
    }


    @SchemaMapping(typeName = "UserProfile", field = "commentCount")
    public Integer commentCount(UserProfileDetail userProfileDetail) {
        return -1;
    }

    @SchemaMapping(typeName = "UserProfile", field = "eventCount")
    public Integer eventCount(UserProfileDetail userProfileDetail) {
        return -1;
    }

    @SchemaMapping(typeName = "UserProfile", field = "postCount")
    public Integer postCount(UserProfileDetail userProfileDetail) {
        return -1;
    }
}
