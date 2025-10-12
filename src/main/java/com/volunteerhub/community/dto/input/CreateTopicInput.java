package com.volunteerhub.community.dto.input;

import com.volunteerhub.community.entity.Event;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateTopicInput {
    private String topicName;
    private Long eventId;
    private UUID createBy;
}
