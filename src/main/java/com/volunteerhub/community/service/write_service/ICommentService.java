package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.dto.ModerationResponse;

import java.util.UUID;

public interface ICommentService {
    ModerationResponse createComment(UUID userId, CreateCommentInput input);
    ModerationResponse  editComment(UUID userId, EditCommentInput input);
    ModerationResponse  deleteComment(UUID userId, Long commentId);
}
