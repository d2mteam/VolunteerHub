package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentResolver {
    private final UserProfileRepository userProfileRepository;

    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount() {
        return -1;
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public UserProfile createBy(Comment comment) {
        return userProfileRepository.findById(comment.getCreatedBy().getUserId()).orElse(null);
    }
}
