package com.volunteerhub.community.dto.graphql.input;

import lombok.Data;

@Data
public class EditCommentInput {
    private Long commentId;
    private String content;
}
