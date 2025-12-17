package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.model.entity.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import org.dataloader.DataLoader;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class CommentResolver {
    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public CompletableFuture<Integer> likeCount(Comment comment,
                                               @org.springframework.graphql.data.method.annotation.DataLoader(name = "commentLikeCountLoader") DataLoader<Long, Integer> likeCountLoader) {
        return likeCountLoader.load(comment.getCommentId());
    }

    @SchemaMapping(typeName = "Comment", field = "createBy")
    public CompletableFuture<UserProfile> createBy(Comment comment,
                                                   @org.springframework.graphql.data.method.annotation.DataLoader(name = "userProfileMiniLoader") DataLoader<UUID, UserProfile> userProfileLoader) {
        UUID creatorId = comment.getCreatedBy() != null ? comment.getCreatedBy().getUserId() : null;
        if (creatorId == null) {
            return CompletableFuture.completedFuture(null);
        }
        return userProfileLoader.load(creatorId);
    }
}
