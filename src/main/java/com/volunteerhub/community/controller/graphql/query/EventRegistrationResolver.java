package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.EventRegistration;
import com.volunteerhub.community.repository.EventRegistrationRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class EventRegistrationResolver {

    private final EventRegistrationRepository eventRegistrationRepository;

    @QueryMapping
    public OffsetPage<EventRegistration> getEventRegistrationByUserId(@Argument int page,
                                                                      @Argument int size,
                                                                      @Argument UUID userId) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<EventRegistration> eventRegistrationPage = eventRegistrationRepository
                .findByUserProfile_UserId(userId, pageable);
        PageInfo pageInfo = PageUtils.from(eventRegistrationPage);

        return OffsetPage.<EventRegistration>builder()
                .content(eventRegistrationPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @QueryMapping
    public OffsetPage<EventRegistration> getEventRegistrationByEventId(@Argument int page,
                                                                       @Argument int size,
                                                                       @Argument Long eventId) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(safePage, safeSize);

        Page<EventRegistration> eventRegistrationPage = eventRegistrationRepository
                .findByEvent_EventId(eventId, pageable);
        PageInfo pageInfo = PageUtils.from(eventRegistrationPage);

        return OffsetPage.<EventRegistration>builder()
                .content(eventRegistrationPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }
}
