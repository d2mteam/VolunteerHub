package com.volunteerhub.community.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationResponse {
    private boolean ok;
    private ModerationAction action;
    private String targetType;
    private String targetId;
    private ModerationStatus status;
    private String message;
    private LocalDateTime moderatedAt;

    public static ModerationResponse success(
            ModerationAction action,
            String targetType,
            String targetId,
            ModerationStatus status,
            String message,
            LocalDateTime moderatedAt
    ) {
        return ModerationResponse.builder()
                .ok(true)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .status(status)
                .message(message)
                .moderatedAt(moderatedAt)
                .build();
    }

    public static ModerationResponse failure(
            ModerationAction action,
            String targetType,
            String targetId,
            String message
    ) {
        return ModerationResponse.builder()
                .ok(false)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .message(message)
                .build();
    }
}
