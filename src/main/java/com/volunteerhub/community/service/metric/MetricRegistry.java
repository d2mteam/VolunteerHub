package com.volunteerhub.community.service.metric;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MetricRegistry {
    private final Map<MetricKey, MetricDescriptor<?, ?>> descriptors;

    public MetricRegistry(List<MetricDescriptor<?, ?>> descriptors) {
        this.descriptors = descriptors.stream()
                .collect(Collectors.toMap(MetricDescriptor::key, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public <P, V> MetricDescriptor<P, V> descriptor(MetricKey key) {
        MetricDescriptor<?, ?> descriptor = descriptors.get(key);
        if (descriptor == null) {
            throw new IllegalArgumentException("No metric descriptor registered for " + key);
        }
        return (MetricDescriptor<P, V>) descriptor;
    }
}
