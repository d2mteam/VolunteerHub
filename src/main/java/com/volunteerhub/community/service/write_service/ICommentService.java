package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.rest.request.CreateCommentInput;
import com.volunteerhub.community.dto.rest.request.EditCommentInput;
import com.volunteerhub.community.dto.rest.response.ModerationResponse;

import java.util.UUID;

public interface ICommentService {
    ModerationResponse createComment(UUID userId, CreateCommentInput input);
    ModerationResponse  editComment(UUID userId, EditCommentInput input);
    ModerationResponse  deleteComment(UUID userId, Long commentId);
}
