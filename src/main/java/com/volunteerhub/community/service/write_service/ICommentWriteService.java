package com.volunteerhub.community.service.write_service;

import com.volunteerhub.community.dto.input.CreateCommentInput;
import com.volunteerhub.community.dto.input.EditCommentInput;

import java.util.UUID;

public interface ICommentWriteService {
    void createComment(UUID userID, CreateCommentInput input);
    void editComment(UUID userID, EditCommentInput input);
    void deleteCommentById(UUID userID, Long commentId);
}
