package com.volunteerhub.community.service.metric;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.repository.LikeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class LikeCountMetricDescriptor<P> implements MetricDescriptor<P, Long> {
    private final LikeRepository likeRepository;
    private final TableType tableType;
    protected final String keyPattern;

    @Override
    public MetricValueType valueType() {
        return MetricValueType.COUNT;
    }

    @Override
    public String redisKey(Long id) {
        return keyPattern.formatted(id);
    }

    @Override
    public Map<Long, Long> loadFromDatabase(List<Long> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }

        return likeRepository.countByTargetIdsAndTableType(ids, tableType)
                .stream()
                .collect(Collectors.toMap(LikeRepository.LikeCountView::getTargetId,
                        LikeRepository.LikeCountView::getCount));
    }
}
