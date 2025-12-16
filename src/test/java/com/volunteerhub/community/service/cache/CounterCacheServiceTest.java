package com.volunteerhub.community.service.cache;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CounterCacheServiceTest {

    private LikeRepository likeRepository;
    private EventRegistrationRepository eventRegistrationRepository;
    private PostRepository postRepository;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private CounterCacheService counterCacheService;

    @BeforeEach
    void setup() {
        likeRepository = Mockito.mock(LikeRepository.class);
        eventRegistrationRepository = Mockito.mock(EventRegistrationRepository.class);
        postRepository = Mockito.mock(PostRepository.class);
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);

        CounterCacheProperties properties = new CounterCacheProperties();
        properties.setRedisTimeout(Duration.ofMillis(200));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.executePipelined(any(SessionCallback.class))).thenReturn(List.of());

        counterCacheService = new CounterCacheService(
                redisTemplate,
                likeRepository,
                eventRegistrationRepository,
                postRepository,
                properties,
                new SimpleMeterRegistry()
        );
    }

    @Test
    void shouldReturnOrderedLikeCountsFromRedis() {
        CounterKey first = new CounterKey(TableType.POST, 1L);
        CounterKey second = new CounterKey(TableType.POST, 2L);

        when(valueOperations.multiGet(anyCollection())).thenReturn(List.of("5", null));

        Map<CounterKey, Integer> result = counterCacheService
                .loadLikeCounts(List.of(first, second))
                .toCompletableFuture()
                .join();

        assertThat(result).containsEntry(first, 5).containsEntry(second, 0);
        assertThat(result.keySet()).containsExactly(first, second);
    }

    @Test
    void shouldFallbackToDatabaseOnRedisFailure() {
        CounterKey key = new CounterKey(TableType.EVENT, 42L);
        when(valueOperations.multiGet(anyCollection())).thenThrow(new RuntimeException("redis down"));
        when(likeRepository.countByTargetIdsAndType(anyCollection(), eq(TableType.EVENT)))
                .thenReturn(List.of(new SimpleTargetProjection(42L, 7L)));

        Map<CounterKey, Integer> result = counterCacheService
                .loadLikeCounts(List.of(key))
                .toCompletableFuture()
                .join();

        assertThat(result).containsEntry(key, 7);
    }

    private record SimpleTargetProjection(Long targetId, Long count) implements TargetCountProjection {
    }
}
