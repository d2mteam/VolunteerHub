package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.configuration.graphql.BatchLoaderConfig;
import com.volunteerhub.community.model.Comment;
import com.volunteerhub.community.model.db_enum.TableType;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import graphql.schema.DataFetchingEnvironment;

import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class CommentResolver {
@SchemaMapping(typeName = "Comment", field = "likeCount")
    public CompletableFuture<Integer> likeCount(Comment comment, DataFetchingEnvironment env) {
        DataLoader<com.volunteerhub.community.service.cache.CounterKey, Integer> dataloader = env.getDataLoader("likeCountLoader");
        return dataloader.load(BatchLoaderConfig.likeKey(TableType.COMMENT, comment.getCommentId()));
    }
}
