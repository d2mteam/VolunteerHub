package com.volunteerhub.community.service.metric;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommentLikeCountMetricDescriptor extends LikeCountMetricDescriptor<Comment> {
    public CommentLikeCountMetricDescriptor(LikeRepository likeRepository,
                                            @Value("${redis.counter.comment-like-key:counter:comment:like:%d}")
                                            String keyPattern) {
        super(likeRepository, TableType.COMMENT, keyPattern);
    }

    @Override
    public MetricKey key() {
        return MetricKey.COMMENT_LIKE_COUNT;
    }

    @Override
    public Long idFromParent(Comment parent) {
        return parent.getCommentId();
    }
}
