package com.volunteerhub.community.service.metric;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Event;
import com.volunteerhub.community.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventLikeCountMetricDescriptor extends LikeCountMetricDescriptor<Event> {
    public EventLikeCountMetricDescriptor(LikeRepository likeRepository,
                                          @Value("${redis.counter.event-like-key:counter:event:like:%d}")
                                          String keyPattern) {
        super(likeRepository, TableType.EVENT, keyPattern);
    }

    @Override
    public MetricKey key() {
        return MetricKey.EVENT_LIKE_COUNT;
    }

    @Override
    public Long idFromParent(Event parent) {
        return parent.getEventId();
    }
}
