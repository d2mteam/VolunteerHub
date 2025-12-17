package com.volunteerhub.community.repository.view;

import com.volunteerhub.community.model.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventPostActivitySummary {
    private Event event;
    private long newPostCount;
    private LocalDateTime latestPostAt;
}
