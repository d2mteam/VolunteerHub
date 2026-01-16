package com.volunteerhub.community.service.metric;

import java.util.List;
import java.util.Map;

public interface MetricDescriptor<P, V> {
    MetricKey key();

    MetricValueType valueType();

    Long idFromParent(P parent);

    String redisKey(Long id);

    Map<Long, V> loadFromDatabase(List<Long> ids);
}
