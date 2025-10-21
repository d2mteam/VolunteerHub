package com.volunteerhub.community.dto.input;

import lombok.Data;

@Data
public class EditCommentInput {
    private Long commentId;
    private String comment;
}
