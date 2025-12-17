package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.RoleInEvent;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.model.graphql.ParticipationHistory;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
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

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserProfileResolver {
    private final UserProfileRepository userProfileRepository;

    private final RoleInEventRepository roleInEventRepository;

    @QueryMapping
    public OffsetPage<UserProfile> findUserProfiles(@Argument Integer page,
                                                    @Argument Integer size) {
        int safePage = page == null ? 0 : Math.max(page, 0);
        int safeSize = size != null && size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<UserProfile> userProfilePage = userProfileRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(userProfilePage);

        return OffsetPage.<UserProfile>builder()
                .content(userProfilePage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @QueryMapping
    public UserProfile getUserProfile(@Argument UUID userId) {
        return userProfileRepository.findById(userId).orElse(null);
    }

    @SchemaMapping(typeName = "UserProfile", field = "participationHistory")
    public OffsetPage<ParticipationHistory> participationHistory(UserProfile userProfile,
                                                                 @Argument Integer page,
                                                                 @Argument Integer size) {
        return getParticipationHistory(userProfile.getUserId(), page, size);
    }

    @QueryMapping
    public OffsetPage<ParticipationHistory> userParticipationHistory(@Argument UUID userId,
                                                                     @Argument Integer page,
                                                                     @Argument Integer size) {
        return getParticipationHistory(userId, page, size);
    }

    private OffsetPage<ParticipationHistory> getParticipationHistory(UUID userId, Integer page, Integer size) {
        int safePage = page == null ? 0 : Math.max(page, 0);
        int safeSize = size != null && size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<RoleInEvent> historyPage = roleInEventRepository.findByUserProfile_UserId(userId, pageable);
        PageInfo pageInfo = PageUtils.from(historyPage);

        return OffsetPage.<ParticipationHistory>builder()
                .content(historyPage
                        .stream()
                        .map(role -> ParticipationHistory.builder()
                                .event(role.getEvent())
                                .eventRole(role.getEventRole())
                                .participationStatus(role.getParticipationStatus())
                                .joinedAt(role.getCreatedAt())
                                .updatedAt(role.getUpdatedAt())
                                .build())
                        .toList())
                .pageInfo(pageInfo)
                .build();
    }
}
