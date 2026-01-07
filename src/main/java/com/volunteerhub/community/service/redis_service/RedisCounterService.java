package com.volunteerhub.community.service.redis_service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCounterService {
    private static final String EVENT_MEMBER_COUNT_KEY = "counter:event:member:%d";
    private static final String EVENT_POST_COUNT_KEY = "counter:event:post:%d";
    private static final String EVENT_COMMENT_COUNT_KEY = "counter:event:comment:%d";
    private static final String EVENT_LIKE_COUNT_KEY = "counter:event:like:%d";
    private static final String EVENT_LATEST_POST_AT_KEY = "counter:event:latestPostAt:%d";
    private static final String EVENT_LATEST_INTERACTION_AT_KEY = "counter:event:latestInteractionAt:%d";
    private static final String POST_COMMENT_COUNT_KEY = "counter:post:comment:%d";
    private static final String POST_LIKE_COUNT_KEY = "counter:post:like:%d";
    private static final String COMMENT_LIKE_COUNT_KEY = "counter:comment:like:%d";
    private static final String DIRTY_EVENT_SUMMARY_KEY = "dirty:event_activity_summary";
    private static final String DIRTY_POST_READ_KEY = "dirty:post_read";

    private final RedisTemplate<String, Object> redisTemplate;

    public long incrementEventMemberCount(Long eventId, long delta) {
        long value = increment(EVENT_MEMBER_COUNT_KEY.formatted(eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventPostCount(Long eventId, long delta) {
        long value = increment(EVENT_POST_COUNT_KEY.formatted(eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventCommentCount(Long eventId, long delta) {
        long value = increment(EVENT_COMMENT_COUNT_KEY.formatted(eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementEventLikeCount(Long eventId, long delta) {
        long value = increment(EVENT_LIKE_COUNT_KEY.formatted(eventId), delta);
        markEventDirty(eventId);
        return value;
    }

    public long incrementPostCommentCount(Long postId, long delta) {
        long value = increment(POST_COMMENT_COUNT_KEY.formatted(postId), delta);
        markPostDirty(postId);
        return value;
    }

    public long incrementPostLikeCount(Long postId, long delta) {
        long value = increment(POST_LIKE_COUNT_KEY.formatted(postId), delta);
        markPostDirty(postId);
        return value;
    }

    public long incrementCommentLikeCount(Long commentId, long delta) {
        return increment(COMMENT_LIKE_COUNT_KEY.formatted(commentId), delta);
    }

    public Optional<Long> getEventMemberCount(Long eventId) {
        return getLong(EVENT_MEMBER_COUNT_KEY.formatted(eventId));
    }

    public Optional<Long> getEventPostCount(Long eventId) {
        return getLong(EVENT_POST_COUNT_KEY.formatted(eventId));
    }

    public Optional<Long> getEventCommentCount(Long eventId) {
        return getLong(EVENT_COMMENT_COUNT_KEY.formatted(eventId));
    }

    public Optional<Long> getEventLikeCount(Long eventId) {
        return getLong(EVENT_LIKE_COUNT_KEY.formatted(eventId));
    }

    public Optional<Long> getPostCommentCount(Long postId) {
        return getLong(POST_COMMENT_COUNT_KEY.formatted(postId));
    }

    public Optional<Long> getPostLikeCount(Long postId) {
        return getLong(POST_LIKE_COUNT_KEY.formatted(postId));
    }

    public Optional<Long> getCommentLikeCount(Long commentId) {
        return getLong(COMMENT_LIKE_COUNT_KEY.formatted(commentId));
    }

    public Optional<LocalDateTime> getEventLatestPostAt(Long eventId) {
        return getTimestamp(EVENT_LATEST_POST_AT_KEY.formatted(eventId));
    }

    public Optional<LocalDateTime> getEventLatestInteractionAt(Long eventId) {
        return getTimestamp(EVENT_LATEST_INTERACTION_AT_KEY.formatted(eventId));
    }

    public void updateEventLatestPostAt(Long eventId, LocalDateTime time) {
        updateMaxTimestamp(EVENT_LATEST_POST_AT_KEY.formatted(eventId), time);
        markEventDirty(eventId);
    }

    public void updateEventLatestInteractionAt(Long eventId, LocalDateTime time) {
        updateMaxTimestamp(EVENT_LATEST_INTERACTION_AT_KEY.formatted(eventId), time);
        markEventDirty(eventId);
    }

    public void markEventDirty(Long eventId) {
        redisTemplate.opsForSet().add(DIRTY_EVENT_SUMMARY_KEY, eventId.toString());
    }

    public void markPostDirty(Long postId) {
        redisTemplate.opsForSet().add(DIRTY_POST_READ_KEY, postId.toString());
    }

    public String eventDirtyKey() {
        return DIRTY_EVENT_SUMMARY_KEY;
    }

    public String postDirtyKey() {
        return DIRTY_POST_READ_KEY;
    }

    public void setEventMemberCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(EVENT_MEMBER_COUNT_KEY.formatted(eventId), count);
    }

    public void setEventPostCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(EVENT_POST_COUNT_KEY.formatted(eventId), count);
    }

    public void setEventCommentCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(EVENT_COMMENT_COUNT_KEY.formatted(eventId), count);
    }

    public void setEventLikeCount(Long eventId, long count) {
        redisTemplate.opsForValue().set(EVENT_LIKE_COUNT_KEY.formatted(eventId), count);
    }

    public void setPostCommentCount(Long postId, long count) {
        redisTemplate.opsForValue().set(POST_COMMENT_COUNT_KEY.formatted(postId), count);
    }

    public void setPostLikeCount(Long postId, long count) {
        redisTemplate.opsForValue().set(POST_LIKE_COUNT_KEY.formatted(postId), count);
    }

    public void setCommentLikeCount(Long commentId, long count) {
        redisTemplate.opsForValue().set(COMMENT_LIKE_COUNT_KEY.formatted(commentId), count);
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
}
