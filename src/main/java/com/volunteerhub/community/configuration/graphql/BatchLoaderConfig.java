package com.volunteerhub.community.configuration.graphql;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.projection.LikeCountProjection;
import com.volunteerhub.community.service.redis_service.RedisEngagementViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.graphql.execution.BatchLoaderRegistryConfigurer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class BatchLoaderConfig implements BatchLoaderRegistryConfigurer {
    private final RedisEngagementViewService redisEngagementViewService;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;

    @Override
    public void configure(BatchLoaderRegistry registry) {
        registerLikeCountLoader(registry, TableType.EVENT, "eventLikeCountLoader");
        registerLikeCountLoader(registry, TableType.POST, "postLikeCountLoader");
        registerLikeCountLoader(registry, TableType.COMMENT, "commentLikeCountLoader");

        registry.forTypePair(UUID.class, UserProfile.class)
                .withName("userProfileMiniLoader")
                .registerMappedBatchLoader((userIds, env) -> CompletableFuture.supplyAsync(() ->
                        userProfileRepository.findAllById(userIds).stream()
                                .collect(Collectors.toMap(UserProfile::getUserId, profile -> profile))
                ));
    }

    private void registerLikeCountLoader(BatchLoaderRegistry registry, TableType tableType, String loaderName) {
        registry.forTypePair(Long.class, Integer.class)
                .withName(loaderName)
                .registerMappedBatchLoader((targetIds, env) -> CompletableFuture.supplyAsync(() -> {
                    Map<Long, Integer> result = new HashMap<>();
                    Map<Long, Long> cachedCounts = redisEngagementViewService.getLikeCounts(tableType, targetIds);
                    List<Long> missingIds = new ArrayList<>();

                    for (Long targetId : targetIds) {
                        Long cached = cachedCounts.get(targetId);
                        if (cached != null) {
                            result.put(targetId, cached.intValue());
                        } else {
                            missingIds.add(targetId);
                        }
                    }

                    if (!missingIds.isEmpty()) {
                        Map<Long, Long> dbCounts = likeRepository.countByTableTypeAndTargetIdIn(tableType, missingIds)
                                .stream()
                                .collect(Collectors.toMap(LikeCountProjection::getTargetId, LikeCountProjection::getCount));

                        dbCounts.forEach((id, count) -> result.put(id, count.intValue()));
                        redisEngagementViewService.setLikeCounts(tableType, dbCounts);
                    }

                    targetIds.forEach(id -> result.putIfAbsent(id, 0));
                    return result;
                }));
    }
}
