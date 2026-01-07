package com.volunteerhub.community.service.metric;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PostLikeCountMetricDescriptor extends LikeCountMetricDescriptor<PostRead> {
    public PostLikeCountMetricDescriptor(LikeRepository likeRepository,
                                         @Value("${redis.counter.post-like-key:counter:post:like:%d}")
                                         String keyPattern) {
        super(likeRepository, TableType.POST, keyPattern);
    }

    @Override
    public MetricKey key() {
        return MetricKey.POST_LIKE_COUNT;
    }

    @Override
    public Long idFromParent(PostRead parent) {
        return parent.getPostId();
    }
}
