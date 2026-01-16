package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.read.EventActivitySummary;
import com.volunteerhub.community.repository.EventActivitySummaryRepository;
import com.volunteerhub.community.module.counter.RedisCounterService;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventActivitySummaryService {
    private final EventActivitySummaryRepository eventActivitySummaryRepository;
    private final UserProfileCacheService userProfileCacheService;
    private final RedisCounterService redisCounterService;

    public void createFromEvent(Event event) {
        var creator = userProfileCacheService.toSummary(event.getCreatedBy());
        EventActivitySummary summary = EventActivitySummary.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventLocation(event.getEventLocation())
                .eventState(event.getEventState())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdById(creator.getUserId())
                .createdByUsername(creator.getUsername())
                .createdByFullName(creator.getFullName())
                .createdByAvatarId(creator.getAvatarId())
                .newMemberCount(0)
                .newPostCount(0)
                .newCommentCount(0)
                .newLikeCount(0)
                .latestPostAt(null)
                .latestInteractionAt(null)
                .build();
        eventActivitySummaryRepository.save(summary);
        redisCounterService.setEventMemberCount(event.getEventId(), 0);
        redisCounterService.setEventPostCount(event.getEventId(), 0);
        redisCounterService.setEventCommentCount(event.getEventId(), 0);
        redisCounterService.setEventLikeCount(event.getEventId(), 0);
        redisCounterService.markEventDirty(event.getEventId());
    }

    public void updateFromEvent(Event event) {
        EventActivitySummary summary = eventActivitySummaryRepository.findById(event.getEventId()).orElse(null);
        if (summary == null) {
            createFromEvent(event);
            return;
        }

        summary.setEventName(event.getEventName());
        summary.setEventDescription(event.getEventDescription());
        summary.setEventLocation(event.getEventLocation());
        summary.setEventState(event.getEventState());
        summary.setUpdatedAt(LocalDateTime.now());
        eventActivitySummaryRepository.save(summary);
    }

    public void deleteByEventId(Long eventId) {
        eventActivitySummaryRepository.deleteById(eventId);
    }
}
