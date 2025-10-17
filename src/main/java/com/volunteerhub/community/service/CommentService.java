package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.CreateEventInput;
import com.volunteerhub.community.dto.input.EditEventInput;
import com.volunteerhub.community.dto.output.CommentDto;

public interface CommentService {
    CommentDto createComment(CreateEventInput input);
    CommentDto editComment(EditEventInput input);
    void deleteComment(Long id);
}
