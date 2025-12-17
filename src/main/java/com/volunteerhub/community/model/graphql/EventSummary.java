package com.volunteerhub.community.model.graphql;

import com.volunteerhub.community.model.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EventSummary {
    private final Long eventId;
    private final String eventName;
    private final LocalDateTime createdAt;
    private final Integer memberCount;
    private final Integer postCount;
    private final Integer likeCount;
    private final UserProfile creatorInfo;
}
