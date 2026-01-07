package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCounterService {
    @Value("${redis.counter.event-member-key:counter:event:member:%d}")
    private String eventMemberCountKey;

    @Value("${redis.counter.event-post-key:counter:event:post:%d}")
    private String eventPostCountKey;

    @Value("${redis.counter.event-comment-key:counter:event:comment:%d}")
    private String eventCommentCountKey;

    @Value("${redis.counter.event-like-key:counter:event:like:%d}")
    private String eventLikeCountKey;

    @Value("${redis.counter.event-latest-post-at-key:counter:event:latestPostAt:%d}")
    private String eventLatestPostAtKey;

    @Value("${redis.counter.event-latest-interaction-at-key:counter:event:latestInteractionAt:%d}")
    private String eventLatestInteractionAtKey;

    @Value("${redis.counter.post-comment-key:counter:post:comment:%d}")
    private String postCommentCountKey;

    @Value("${redis.counter.post-like-key:counter:post:like:%d}")
    private String postLikeCountKey;

    @Value("${redis.counter.comment-like-key:counter:comment:like:%d}")
    private String commentLikeCountKey;

    @Value("${redis.counter.dirty-event-summary-key:dirty:event_activity_summary}")
    private String dirtyEventSummaryKey;

    @Value("${redis.counter.dirty-post-read-key:dirty:post_read}")
    private String dirtyPostReadKey;

    private final RedisTemplate<String, Object> redisTemplate;

    public long incrementEventMemberCount(Long eventId, long delta) {
        long value = increment(format(eventMemberCountKey, eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventPostCount(Long eventId, long delta) {
        long value = increment(format(eventPostCountKey, eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventCommentCount(Long eventId, long delta) {
        long value = increment(format(eventCommentCountKey, eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventLikeCount(Long eventId, long delta) {
        long value = increment(format(eventLikeCountKey, eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementPostCommentCount(Long postId, long delta) {
        long value = increment(format(postCommentCountKey, postId), delta);
        markPostDirty(postId);
        return value;
    }

    public long incrementPostLikeCount(Long postId, long delta) {
        long value = increment(format(postLikeCountKey, postId), delta);
        markPostDirty(postId);
        return value;
    }

    public long incrementCommentLikeCount(Long commentId, long delta) {
        return increment(format(commentLikeCountKey, commentId), delta);
    }

    public Optional<Long> getEventMemberCount(Long eventId) {
        return getLong(format(eventMemberCountKey, eventId));
    }

    public Optional<Long> getEventPostCount(Long eventId) {
        return getLong(format(eventPostCountKey, eventId));
    }

    public Optional<Long> getEventCommentCount(Long eventId) {
        return getLong(format(eventCommentCountKey, eventId));
    }

    public Optional<Long> getEventLikeCount(Long eventId) {
        return getLong(format(eventLikeCountKey, eventId));
    }

    public Optional<Long> getPostCommentCount(Long postId) {
        return getLong(format(postCommentCountKey, postId));
    }

    public Optional<Long> getPostLikeCount(Long postId) {
        return getLong(format(postLikeCountKey, postId));
    }

    public Optional<Long> getCommentLikeCount(Long commentId) {
        return getLong(format(commentLikeCountKey, commentId));
    }

    public Optional<LocalDateTime> getEventLatestPostAt(Long eventId) {
        return getTimestamp(format(eventLatestPostAtKey, eventId));
    }

    public Optional<LocalDateTime> getEventLatestInteractionAt(Long eventId) {
        return getTimestamp(format(eventLatestInteractionAtKey, eventId));
    }

    public void updateEventLatestPostAt(Long eventId, LocalDateTime time) {
        updateMaxTimestamp(format(eventLatestPostAtKey, eventId), time);
        markEventDirty(eventId);
    }

    public void updateEventLatestInteractionAt(Long eventId, LocalDateTime time) {
        updateMaxTimestamp(format(eventLatestInteractionAtKey, eventId), time);
        markEventDirty(eventId);
    }

    public void markEventDirty(Long eventId) {
        redisTemplate.opsForSet().add(dirtyEventSummaryKey, eventId.toString());
    }

    public void markPostDirty(Long postId) {
        redisTemplate.opsForSet().add(dirtyPostReadKey, postId.toString());
    }

    public String eventDirtyKey() {
        return dirtyEventSummaryKey;
    }

    public String postDirtyKey() {
        return dirtyPostReadKey;
    }

    public void setEventMemberCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(format(eventMemberCountKey, eventId), count);
    }

    public void setEventPostCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(format(eventPostCountKey, eventId), count);
    }

    public void setEventCommentCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(format(eventCommentCountKey, eventId), count);
    }

    public void setEventLikeCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(format(eventLikeCountKey, eventId), count);
    }

    public void setPostCommentCount(Long postId, long count) {
        redisTemplate.opsForValue().set(format(postCommentCountKey, postId), count);
    }

    public void setPostLikeCount(Long postId, long count) {
        redisTemplate.opsForValue().set(format(postLikeCountKey, postId), count);
    }

    public void setCommentLikeCount(Long commentId, long count) {
        redisTemplate.opsForValue().set(format(commentLikeCountKey, commentId), count);
    }

    private long increment(String key, long delta) {
        Long value = redisTemplate.opsForValue().increment(key, delta);
        return value == null ? 0 : value;
    }

    private Optional<Long> getLong(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(value.toString()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<LocalDateTime> getTimestamp(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            long epochMillis = Long.parseLong(value.toString());
            return Optional.of(LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(epochMillis),
                    ZoneId.systemDefault()
            ));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private void updateMaxTimestamp(String key, LocalDateTime time) {
        if (time == null) {
            return;
        }
        long candidate = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Optional<Long> current = getLong(key);
        if (current.isEmpty() || candidate > current.get()) {
            redisTemplate.opsForValue().set(key, candidate);
        }
    }

    private String format(String pattern, Long id) {
        return pattern.formatted(id);
    }
}
