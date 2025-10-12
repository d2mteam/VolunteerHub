package com.volunteerhub.community.mapper;

import com.volunteerhub.community.dto.output.CommentDto;
import com.volunteerhub.community.entity.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment entity) {
        return CommentDto.builder()
                .commentId(entity.getCommentId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .content(entity.getContent())
                .build();
    }

    public static Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .commentId(dto.getCommentId())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .content(dto.getContent())
                .build();
    }
}
