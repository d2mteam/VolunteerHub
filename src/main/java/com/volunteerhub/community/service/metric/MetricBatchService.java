package com.volunteerhub.community.service.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricBatchService {
    private final MetricRegistry metricRegistry;
    private final RedisMetricCache redisMetricCache;

    public <P> Map<P, Integer> loadCountMetric(MetricKey key, List<P> parents) {
        MetricDescriptor<P, Long> descriptor = metricRegistry.descriptor(key);
        List<Long> ids = parents.stream()
                .map(descriptor::idFromParent)
                .toList();

        Map<Long, Optional<Long>> cached = redisMetricCache.multiGet(ids, descriptor);
        List<Long> missingIds = ids.stream()
                .filter(id -> cached.getOrDefault(id, Optional.empty()).isEmpty())
                .distinct()
                .toList();

        Map<Long, Long> dbResults = missingIds.isEmpty()
                ? Map.of()
                : descriptor.loadFromDatabase(missingIds);

        Map<Long, Long> writeBack = new HashMap<>();
        if (!missingIds.isEmpty()) {
            for (Long id : missingIds) {
                Long value = dbResults.getOrDefault(id, 0L);
                writeBack.put(id, value);
            }
            redisMetricCache.multiSet(writeBack, descriptor);
        }

        Map<Long, Long> merged = new HashMap<>();
        cached.forEach((id, value) -> value.ifPresent(v -> merged.put(id, v)));
        merged.putAll(dbResults);

        return parents.stream()
                .collect(Collectors.toMap(
                        parent -> parent,
                        parent -> Math.toIntExact(merged.getOrDefault(descriptor.idFromParent(parent), 0L))
                ));
    }
}
