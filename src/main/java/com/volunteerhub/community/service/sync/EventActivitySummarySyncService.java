package com.volunteerhub.community.service.sync;

import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.model.read.EventActivitySummary;
import com.volunteerhub.community.repository.EventActivitySummaryRepository;
import com.volunteerhub.community.repository.EventRepository;
import com.volunteerhub.community.module.counter.RedisCounterService;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventActivitySummarySyncService {
    private final RedisCounterService redisCounterService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EventActivitySummaryRepository eventActivitySummaryRepository;
    private final EventRepository eventRepository;
    private final UserProfileCacheService userProfileCacheService;

    @Scheduled(fixedDelayString = "${readmodel.event-activity-sync-ms:30000}")
    @Transactional
    public void sync() {
        Set<Object> dirtyIds = redisTemplate.opsForSet().members(redisCounterService.eventDirtyKey());
        if (dirtyIds == null || dirtyIds.isEmpty()) {
            return;
        }

        for (Object rawId : dirtyIds) {
            Long eventId;
            try {
                eventId = Long.valueOf(rawId.toString());
            } catch (NumberFormatException ex) {
                redisTemplate.opsForSet().remove(redisCounterService.eventDirtyKey(), rawId);
                continue;
            }

            EventActivitySummary summary = eventActivitySummaryRepository.findById(eventId)
                    .orElseGet(() -> createFromEvent(eventId));

            if (summary == null) {
                redisTemplate.opsForSet().remove(redisCounterService.eventDirtyKey(), rawId);
                continue;
            }

            summary.setNewMemberCount(redisCounterService.getEventMemberCount(eventId).orElse(summary.getNewMemberCount()));
            summary.setNewPostCount(redisCounterService.getEventPostCount(eventId).orElse(summary.getNewPostCount()));
            summary.setNewCommentCount(redisCounterService.getEventCommentCount(eventId).orElse(summary.getNewCommentCount()));
            summary.setNewLikeCount(redisCounterService.getEventLikeCount(eventId).orElse(summary.getNewLikeCount()));
            summary.setLatestPostAt(redisCounterService.getEventLatestPostAt(eventId).orElse(summary.getLatestPostAt()));
            summary.setLatestInteractionAt(redisCounterService.getEventLatestInteractionAt(eventId).orElse(summary.getLatestInteractionAt()));
            summary.setUpdatedAt(LocalDateTime.now());
            eventActivitySummaryRepository.save(summary);

            redisTemplate.opsForSet().remove(redisCounterService.eventDirtyKey(), rawId);
        }
    }

    private EventActivitySummary createFromEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return null;
        }

        var summary = userProfileCacheService.toSummary(event.getCreatedBy());
        EventActivitySummary result = EventActivitySummary.builder()
                .eventId(event.getEventId())
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventLocation(event.getEventLocation())
                .eventState(event.getEventState())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdById(summary.getUserId())
                .createdByUsername(summary.getUsername())
                .createdByFullName(summary.getFullName())
                .createdByAvatarId(summary.getAvatarId())
                .newMemberCount(0)
                .newPostCount(0)
                .newCommentCount(0)
                .newLikeCount(0)
                .latestPostAt(null)
                .latestInteractionAt(null)
                .build();
        return eventActivitySummaryRepository.save(result);
    }
}
