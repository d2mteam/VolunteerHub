//package com.volunteerhub.community.configuration.graphql;
//
//
//import com.volunteerhub.community.configuration.graphql.count_cache.DatabaseLoader;
//import com.volunteerhub.community.service.redis_service.RedisCountCacheService;
//import com.volunteerhub.community.configuration.graphql.count_cache.CountDefinition;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.graphql.execution.BatchLoaderRegistry;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.time.Duration;
//import java.util.*;
//
//@Configuration
//public class BatchLoaderConfig {
//    private final RedisCountCacheService redisCountCacheService;
//    private final DatabaseLoader databaseLoader;
//
//    public BatchLoaderConfig(BatchLoaderRegistry registry,
//                             RedisCountCacheService redisCountCacheService,
//                             List<CountDefinition<Long>> countDefinitions,
//                                DatabaseLoader databaseLoader) {
//        this.redisCountCacheService = redisCountCacheService;
//        this.databaseLoader = databaseLoader;
//
//        for (CountDefinition<Long> countDefinition : countDefinitions) {
//            registry.forTypePair(Long.class, Integer.class)
//                    .withName(countDefinition.name())
//                    .registerMappedBatchLoader((postIds, env)
//                            -> loadCount(postIds, countDefinition));
//        }
//    }
//
//    public Mono<Map<Long, Integer>> loadCount(
//            Set<Long> postIds,
//            CountDefinition<Long> countDefinition
//    ) {
//        List<Long> ids = new ArrayList<>(postIds);
//
//        List<String> redisKeys = ids.stream()
//                .map(id -> countDefinition.keyBuilder().build(id))
//                .toList();
//
//        return Mono.fromCallable(() -> {
//
//            Map<Long, Integer> result = new HashMap<>();
//            List<Integer> redisValues;
//
//            // 1️⃣ Try Redis
//            try {
//                redisValues = redisCountCacheService.get(redisKeys);
//            } catch (Exception e) {
//                // Redis DOWN → DB ALL
//                return databaseLoader.load(ids, countDefinition);
//            }
//
//            // 2️⃣ Find miss
//            List<Long> missIds = new ArrayList<>();
//
//            for (int i = 0; i < ids.size(); i++) {
//                Integer v = redisValues.get(i);
//                if (v != null) {
//                    result.put(ids.get(i), v);
//                } else {
//                    missIds.add(ids.get(i));
//                }
//            }
//
//            // 3️⃣ DB fallback only for miss
//            if (!missIds.isEmpty()) {
//                Map<Long, Integer> dbResult =
//                        databaseLoader.load(ids, countDefinition);
//
//                result.putAll(dbResult);
//
//                // 4️⃣ Best-effort backfill Redis
//                redisCountCacheService.set(
//                        missIds.stream()
//                                .map(id -> countDefinition.keyBuilder().build(id))
//                                .toList(),
//                        missIds.stream()
//                                .map(id -> dbResult.getOrDefault(id, 0))
//                                .toList(),
//                        Duration.ofSeconds(countDefinition.cachePolicy().ttl())
//                );
//            }
//
//            // 5️⃣ Fill missing = 0
//            ids.forEach(id -> result.putIfAbsent(id, 0));
//            return result;
//
//        }).subscribeOn(Schedulers.boundedElastic());
//    }
//}