package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.RoleInEvent;
import com.volunteerhub.community.repository.RoleInEventRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class HistoryResolver {

    private final RoleInEventRepository roleInEventRepository;

    @QueryMapping
    public OffsetPage<RoleInEvent> listMemberInEvent(@Argument Long eventId,
                                                     @Argument Integer page,
                                                     @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<RoleInEvent> memberPage = roleInEventRepository.findByEvent_EventId(eventId, pageable);
        PageInfo pageInfo = PageUtils.from(memberPage);
        return OffsetPage.<RoleInEvent>builder()
                .content(memberPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @QueryMapping
    public OffsetPage<RoleInEvent> userHistory(@AuthenticationPrincipal UUID userId,
                                               @Argument Integer page,
                                               @Argument Integer size) {
        int safePage = page != null ? Math.max(page, 0) : 0;
        int safeSize = size != null && size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<RoleInEvent> memberPage = roleInEventRepository.findByUserProfile_UserId(userId, pageable);
        PageInfo pageInfo = PageUtils.from(memberPage);
        return OffsetPage.<RoleInEvent>builder()
                .content(memberPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }
}
