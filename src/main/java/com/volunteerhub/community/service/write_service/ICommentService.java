package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;

import java.util.UUID;

public interface ICommentService {
    void createComment(UUID userId, CreateCommentInput input);

    void editComment(UUID userId, EditCommentInput input);

    void deleteComment(UUID userId, Long commentId);
}
