package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.readmodel.CommentReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.service.readmodel.CommentReadModelService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentResolver {
    private final CommentReadModelService commentReadModelService;

    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount(CommentReadModel comment) {
        CommentReadModel model = commentReadModelService.getComment(comment.getCommentId());
        return model != null ? model.getLikeCount() : 0;
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public UserProfileSummaryView createBy(CommentReadModel comment) {
        CommentReadModel model = commentReadModelService.getComment(comment.getCommentId());
        return model != null ? model.getCreatedBy() : null;
    }
}
