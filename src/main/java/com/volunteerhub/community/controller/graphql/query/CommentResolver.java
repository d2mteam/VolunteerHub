package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.graphql.output.UserProfileSummaryView;
import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.service.redis_service.RedisCounterService;
import com.volunteerhub.community.service.redis_service.UserProfileCacheService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentResolver {
    private final LikeRepository likeRepository;
    private final RedisCounterService redisCounterService;
    private final UserProfileCacheService userProfileCacheService;

    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount(Comment comment) {
        return redisCounterService.getCommentLikeCount(comment.getCommentId())
                .map(Long::intValue)
                .orElseGet(() -> {
                    int count = likeRepository.countByTargetIdAndTableType(comment.getCommentId(), TableType.COMMENT);
                    redisCounterService.setCommentLikeCount(comment.getCommentId(), count);
                    return count;
                });
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public UserProfileSummaryView createBy(Comment comment) {
        return userProfileCacheService.getSummary(comment.getCreatedBy().getUserId());
    }
}
