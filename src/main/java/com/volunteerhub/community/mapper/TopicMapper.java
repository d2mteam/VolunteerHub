package com.volunteerhub.community.mapper;

import com.volunteerhub.community.dto.output.TopicDto;
import com.volunteerhub.community.entity.Topic;

public class TopicMapper {
    public static TopicDto toDto(Topic entity) {
        return TopicDto.builder()
                .topicId(entity.getTopicId())
                .topicName(entity.getTopicName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public static Topic toEntity(TopicDto dto) {
        return Topic.builder()
                .topicId(dto.getTopicId())
                .topicName(dto.getTopicName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
    
    
}
