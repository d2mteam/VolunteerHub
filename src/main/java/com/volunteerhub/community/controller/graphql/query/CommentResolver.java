package com.volunteerhub.community.controller.graphql.query;

import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class CommentResolver {
    @SchemaMapping(typeName = "Comment", field = "likeCount")
    public Integer likeCount() {
        return -1;
    }
}
