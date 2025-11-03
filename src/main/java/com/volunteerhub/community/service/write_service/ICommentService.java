package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;

import java.util.UUID;

public interface ICommentService {
    ActionResponse<Void> createComment(UUID userId, CreateCommentInput input);
    ActionResponse<Void>  editComment(UUID userId, EditCommentInput input);
    ActionResponse<Void>  deleteComment(UUID userId, Long commentId);
}
