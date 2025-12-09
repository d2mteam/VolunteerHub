package com.volunteerhub.community.controller.graphql.mapper;

import com.volunteerhub.community.dto.graphql.type.EventSummary;
import com.volunteerhub.community.dto.graphql.type.UserProfileMini;
import com.volunteerhub.community.model.Event;

import java.util.Map;
import java.util.Optional;

public final class EventSummaryMapper {
    private EventSummaryMapper() {
    }

    public static EventSummary toSummary(Event event,
                                         Map<Long, Long> memberCounts,
                                         Map<Long, Long> postCounts,
                                         Map<Long, Long> likeCounts) {
        return EventSummary.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .createdAt(event.getCreatedAt())
                .memberCount(extractCount(memberCounts, event.getEventId()))
                .postCount(extractCount(postCounts, event.getEventId()))
                .likeCount(extractCount(likeCounts, event.getEventId()))
                .creatorInfo(Optional.ofNullable(event.getCreatedBy())
                        .map(user -> UserProfileMini.builder()
                                .userId(user.getUserId())
                                .userName(user.getUsername())
                                .avatarId(user.getAvatarId())
                                .build())
                        .orElse(null))
                .build();
    }

    private static int extractCount(Map<Long, Long> counts, Long id) {
        return counts.getOrDefault(id, 0L).intValue();
    }
}
