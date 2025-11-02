package com.volunteerhub.community.dto.graphql.output;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MutationResult {
    private boolean ok;
    private String message;
    private String id;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
