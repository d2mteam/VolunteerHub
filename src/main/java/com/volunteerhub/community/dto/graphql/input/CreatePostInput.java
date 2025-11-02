package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePostInput {
    private Long eventId;
    private String content;
}
