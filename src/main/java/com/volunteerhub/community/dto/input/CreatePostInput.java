package com.volunteerhub.community.dto.input;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CreatePostInput {
    private Long topicId;
    private String content;
    private UUID createBy;
}
