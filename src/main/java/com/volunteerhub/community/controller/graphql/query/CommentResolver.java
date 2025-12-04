package com.volunteerhub.community.controller.graphql.query;

import org.springframework.graphql.data.method.annotation.SchemaMapping;

public class CommentResolver {

    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount() {
        return -1;
    }
}
