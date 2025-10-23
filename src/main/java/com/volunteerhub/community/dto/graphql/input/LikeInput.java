package com.volunteerhub.community.dto.graphql.input;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeInput {
    private Long targetId;
    private String targetType;
}
