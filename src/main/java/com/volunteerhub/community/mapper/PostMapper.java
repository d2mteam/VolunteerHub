package com.volunteerhub.community.mapper;

import com.volunteerhub.community.dto.output.PostDto;
import com.volunteerhub.community.entity.Post;

public class PostMapper {
    public static PostDto toDto(Post entity) {
        return PostDto.builder()
                .postId(entity.getPostId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
    
    public static Post toEntity(PostDto dto) {
        return Post.builder()
                .postId(dto.getPostId())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(dto.getCreatedBy())
                .build();
    }
}
