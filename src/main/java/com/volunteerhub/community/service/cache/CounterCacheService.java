package com.volunteerhub.community.service.cache;

import com.volunteerhub.community.model.db_enum.RegistrationStatus;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CounterCacheService {
    private static final Logger log = LoggerFactory.getLogger(CounterCacheService.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final LikeRepository likeRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final PostRepository postRepository;
    private final CounterCacheProperties properties;
    private final MeterRegistry meterRegistry;

    private final ExecutorService redisExecutor = Executors.newCachedThreadPool();
    private final SimpleCircuitBreaker breaker;
    private final SingleFlight singleFlight;

    public CounterCacheService(StringRedisTemplate stringRedisTemplate,
                               LikeRepository likeRepository,
                               EventRegistrationRepository eventRegistrationRepository,
                               PostRepository postRepository,
                               CounterCacheProperties properties,
                               MeterRegistry meterRegistry) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.likeRepository = likeRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.postRepository = postRepository;
        this.properties = properties;
        this.meterRegistry = meterRegistry;
        this.breaker = new SimpleCircuitBreaker(properties.getBreakerFailureThreshold(), properties.getBreakerOpenDuration());
        this.singleFlight = new SingleFlight(properties.getSingleFlightWindow());
    }

    public CompletionStage<Map<CounterKey, Integer>> loadLikeCounts(List<CounterKey> keys) {
        return loadCounters(keys,
                properties.isEnableLikeCache(),
                properties.getLikeTtl(),
                this::redisReadLikes,
                miss -> singleFlight.run("db_like:" + miss.toString(), () -> CompletableFuture.supplyAsync(() -> dbReadLikes(miss))),
                this::redisBackfillLikes,
                "likeCount");
    }

    public CompletionStage<Map<Long, Integer>> loadMemberCounts(List<Long> eventIds) {
        return loadCounters(eventIds,
                properties.isEnableMemberCache(),
                properties.getMemberTtl(),
                this::redisReadMembers,
                miss -> singleFlight.run("db_member:" + miss.toString(), () -> CompletableFuture.supplyAsync(() -> dbReadMembers(miss))),
                this::redisBackfillMembers,
                "memberCount");
    }

    public CompletionStage<Map<Long, Integer>> loadPostCounts(List<Long> eventIds) {
        return loadCounters(eventIds,
                properties.isEnablePostCache(),
                properties.getPostTtl(),
                this::redisReadPosts,
                miss -> singleFlight.run("db_post:" + miss.toString(), () -> CompletableFuture.supplyAsync(() -> dbReadPosts(miss))),
                this::redisBackfillPosts,
                "postCount");
    }

    private <K> CompletionStage<Map<K, Integer>> loadCounters(List<K> keys,
                                                              boolean cacheEnabled,
                                                              Duration ttl,
                                                              Function<Set<K>, Map<K, Integer>> redisFetcher,
                                                              Function<Set<K>, CompletableFuture<Map<K, Integer>>> dbFetcher,
                                                              BiConsumerWithException<Map<K, Integer>, Duration> backfill,
                                                              String metricPrefix) {
        Set<K> unique = new LinkedHashSet<>(keys);
        Map<K, Integer> ordered = new LinkedHashMap<>();

        Set<K> misses = new LinkedHashSet<>(unique);
        if (cacheEnabled && breaker.allowRequest()) {
            Timer.Sample sample = Timer.start(meterRegistry);
            try {
                Map<K, Integer> hits = redisFetcher.apply(unique);
                sample.stop(meterRegistry.timer("counter_cache_redis_latency", "counter", metricPrefix));
                meterRegistry.counter("counter_cache_hit_total", "counter", metricPrefix).increment(hits.size());
                ordered.putAll(hits);
                misses.removeAll(hits.keySet());
                breaker.recordSuccess();
            } catch (Exception ex) {
                meterRegistry.counter("counter_cache_redis_error_total", "counter", metricPrefix).increment();
                breaker.recordFailure();
                log.warn("Redis read failed for {}: {}", metricPrefix, ex.getMessage());
            }
        } else {
            meterRegistry.counter("counter_cache_breaker_open_total", "counter", metricPrefix).increment();
        }

        if (misses.isEmpty()) {
            return CompletableFuture.completedFuture(fillZeros(keys, ordered));
        }

        meterRegistry.counter("counter_cache_db_fallback_total", "counter", metricPrefix).increment();
        Timer.Sample dbSample = Timer.start(meterRegistry);
        return dbFetcher.apply(misses).handle((dbResult, ex) -> {
            dbSample.stop(meterRegistry.timer("counter_cache_db_latency", "counter", metricPrefix));
            if (ex != null) {
                log.warn("DB fallback failed for {}: {}", metricPrefix, ex.getMessage());
                return fillZeros(keys, ordered);
            }
            ordered.putAll(dbResult);
            if (cacheEnabled && !breaker.isOpen()) {
                try {
                    backfill.accept(dbResult, ttl);
                } catch (Exception e) {
                    log.warn("Backfill skipped for {}: {}", metricPrefix, e.getMessage());
                }
            }
            return fillZeros(keys, ordered);
        });
    }

    private Map<CounterKey, Integer> redisReadLikes(Set<CounterKey> keys) {
        return redisRead(keys, this::likeCacheKey);
    }

    private Map<Long, Integer> redisReadMembers(Set<Long> ids) {
        return redisRead(ids, this::memberCacheKey);
    }

    private Map<Long, Integer> redisReadPosts(Set<Long> ids) {
        return redisRead(ids, this::postCacheKey);
    }

    private <K> Map<K, Integer> redisRead(Set<K> ids, Function<K, String> keyMapper) {
        List<String> keys = ids.stream().map(keyMapper).toList();
        try {
            List<String> values = CompletableFuture
                    .supplyAsync(() -> stringRedisTemplate.opsForValue().multiGet(keys), redisExecutor)
                    .orTimeout(properties.getRedisTimeout().toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
                    .join();
            Map<K, Integer> result = new HashMap<>();
            if (values == null) {
                return result;
            }
            int index = 0;
            for (K id : ids) {
                String raw = values.get(index++);
                if (raw != null) {
                    result.put(id, Integer.parseInt(raw));
                }
            }
            return result;
        } catch (Exception ex) {
            if (ex instanceof RedisConnectionFailureException) {
                meterRegistry.counter("counter_cache_redis_timeout_total").increment();
            }
            throw ex;
        }
    }

    private Map<CounterKey, Integer> dbReadLikes(Set<CounterKey> misses) {
        Map<TableType, List<Long>> byType = misses.stream()
                .collect(Collectors.groupingBy(CounterKey::targetType, Collectors.mapping(CounterKey::targetId, Collectors.toList())));
        Map<CounterKey, Integer> result = new HashMap<>();
        byType.forEach((type, ids) -> {
            List<TargetCountProjection> counts = likeRepository.countByTargetIdsAndType(ids, type);
            Map<Long, Long> asMap = counts.stream().collect(Collectors.toMap(TargetCountProjection::getTargetId, TargetCountProjection::getCount));
            ids.forEach(id -> result.put(new CounterKey(type, id), Math.toIntExact(asMap.getOrDefault(id, 0L))));
        });
        return result;
    }

    private Map<Long, Integer> dbReadMembers(Set<Long> misses) {
        List<EventRegistrationCountProjection> counts = eventRegistrationRepository
                .countByEventIdsAndStatus(misses, RegistrationStatus.APPROVED);
        Map<Long, Long> asMap = counts.stream().collect(Collectors.toMap(EventRegistrationCountProjection::getEventId, EventRegistrationCountProjection::getCount));
        Map<Long, Integer> result = new HashMap<>();
        misses.forEach(id -> result.put(id, Math.toIntExact(asMap.getOrDefault(id, 0L))));
        return result;
    }

    private Map<Long, Integer> dbReadPosts(Set<Long> misses) {
        List<PostCountProjection> counts = postRepository.countByEventIds(misses);
        Map<Long, Long> asMap = counts.stream().collect(Collectors.toMap(PostCountProjection::getEventId, PostCountProjection::getCount));
        Map<Long, Integer> result = new HashMap<>();
        misses.forEach(id -> result.put(id, Math.toIntExact(asMap.getOrDefault(id, 0L))));
        return result;
    }

    private void redisBackfillLikes(Map<CounterKey, Integer> data, Duration ttl) {
        redisBackfill(data, ttl, this::likeCacheKey);
    }

    private void redisBackfillMembers(Map<Long, Integer> data, Duration ttl) {
        redisBackfill(data, ttl, this::memberCacheKey);
    }

    private void redisBackfillPosts(Map<Long, Integer> data, Duration ttl) {
        redisBackfill(data, ttl, this::postCacheKey);
    }

    private <K> void redisBackfill(Map<K, Integer> data, Duration ttl, Function<K, String> keyMapper) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        Map<String, String> payload = data.entrySet().stream()
                .collect(Collectors.toMap(entry -> keyMapper.apply(entry.getKey()), entry -> entry.getValue().toString()));
        stringRedisTemplate.executePipelined(connection -> {
            payload.forEach((key, value) -> {
                connection.stringCommands().set(key.getBytes(), value.getBytes());
                connection.keyCommands().expire(key.getBytes(), ttl.toSeconds());
            });
            return null;
        });
    }

    private Map<CounterKey, Integer> fillZeros(List<CounterKey> order, Map<CounterKey, Integer> partial) {
        Map<CounterKey, Integer> result = new LinkedHashMap<>();
        order.forEach(key -> result.put(key, partial.getOrDefault(key, 0)));
        return result;
    }

    private Map<Long, Integer> fillZeros(List<Long> order, Map<Long, Integer> partial) {
        Map<Long, Integer> result = new LinkedHashMap<>();
        order.forEach(key -> result.put(key, partial.getOrDefault(key, 0)));
        return result;
    }

    private String likeCacheKey(CounterKey key) {
        return switch (key.targetType()) {
            case POST -> "post:" + key.targetId() + ":likeCount";
            case COMMENT -> "comment:" + key.targetId() + ":likeCount";
            case EVENT -> "event:" + key.targetId() + ":likeCount";
            default -> "like:" + key.targetType().name().toLowerCase() + ":" + key.targetId();
        };
    }

    private String memberCacheKey(Long eventId) {
        return "event:" + eventId + ":memberCount";
    }

    private String postCacheKey(Long eventId) {
        return "event:" + eventId + ":postCount";
    }

    @FunctionalInterface
    private interface BiConsumerWithException<T, U> {
        void accept(T t, U u) throws Exception;
    }
}
