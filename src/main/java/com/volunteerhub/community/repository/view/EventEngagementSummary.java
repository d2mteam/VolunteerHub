package com.volunteerhub.community.repository.view;

import com.volunteerhub.community.model.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventEngagementSummary {
    private Event event;
    private long newMemberCount;
    private long newCommentCount;
    private long newLikeCount;
    private LocalDateTime latestInteractionAt;
}
