package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.graphql.output.UserProfileSummaryView;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.service.metric.MetricBatchService;
import com.volunteerhub.community.service.metric.MetricKey;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CommentResolver {
    private final MetricBatchService metricBatchService;
    private final UserProfileCacheService userProfileCacheService;

    @BatchMapping(typeName = "Comment", field = "likeCount")
    public Map<Comment, Integer> likeCount(List<Comment> comments) {
        return metricBatchService.loadCountMetric(MetricKey.COMMENT_LIKE_COUNT, comments);
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public UserProfileSummaryView createBy(Comment comment) {
        return userProfileCacheService.getSummary(comment.getCreatedBy().getUserId());
    }
}
