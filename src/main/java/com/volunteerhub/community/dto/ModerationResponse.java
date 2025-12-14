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
    private ModerationResult result;
    private ModerationAction action;
    private ModerationTargetType targetType;
    private String targetId;
    private ModerationStatus status;
    private String message;
    private String reasonCode;
    private LocalDateTime moderatedAt;

    public static ModerationResponse success(
            ModerationAction action,
            ModerationTargetType targetType,
            String targetId,
            ModerationStatus status,
            String message
    ) {
        return success(action, targetType, targetId, status, message, null);
    }

    public static ModerationResponse success(
            ModerationAction action,
            ModerationTargetType targetType,
            String targetId,
            ModerationStatus status,
            String message,
            String reasonCode
    ) {
        return ModerationResponse.builder()
                .result(ModerationResult.SUCCESS)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .status(status)
                .message(message)
                .reasonCode(reasonCode)
                .moderatedAt(LocalDateTime.now())
                .build();
    }

    public static ModerationResponse failure(
            ModerationAction action,
            ModerationTargetType targetType,
            String targetId,
            ModerationResult result,
            ModerationStatus status,
            String message,
            String reasonCode
    ) {
        return ModerationResponse.builder()
                .result(result)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .status(status)
                .message(message)
                .reasonCode(reasonCode)
                .moderatedAt(LocalDateTime.now())
                .build();
    }
}
