package com.volunteerhub.community.dto.output;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TopicDto {
    private Long topicId;
    private String topicName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
