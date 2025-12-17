package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentResolver {
    private final UserProfileRepository userProfileRepository;
    private final LikeRepository likeRepository;

    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount(Comment comment) {
        return likeRepository.countByTargetIdAndTableType(comment.getCommentId(), TableType.COMMENT);
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public UserProfile createBy(Comment comment) {
        return userProfileRepository.findById(comment.getCreatedBy().getUserId()).orElse(null);
    }
}
