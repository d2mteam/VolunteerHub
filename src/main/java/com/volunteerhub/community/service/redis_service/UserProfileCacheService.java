package com.volunteerhub.community.service.redis_service;

import com.volunteerhub.community.dto.graphql.output.UserProfileSummaryView;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileCacheService {
    @Value("${redis.userprofile-summary-ttl-minutes:10}")
    private long cacheTtlMinutes;

    @Value("${redis.userprofile-summary-key:userprofile:summary:%s}")
    private String cacheKeyPattern;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserProfileRepository userProfileRepository;

    public UserProfileSummaryView getSummary(UUID userId) {
        String key = cacheKeyPattern.formatted(userId);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof UserProfileSummaryView summary) {
            return summary;
        }

        Optional<UserProfile> profile = userProfileRepository.findById(userId);
        if (profile.isEmpty()) {
            return null;
        }

        UserProfileSummaryView summary = toSummary(profile.get());
        redisTemplate.opsForValue().set(key, summary, Duration.ofMinutes(cacheTtlMinutes));
        return summary;
    }

    public UserProfileSummaryView toSummary(UserProfile profile) {
        return UserProfileSummaryView.builder()
                .userId(profile.getUserId())
                .username(profile.getUsername())
                .fullName(profile.getFullName())
                .avatarId(profile.getAvatarId())
                .build();
    }
}
