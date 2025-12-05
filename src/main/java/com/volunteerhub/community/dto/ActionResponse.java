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
public class ActionResponse<T> {
    private boolean ok;
    private String message;
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private T data;

    public static <T> ActionResponse<T> success(String id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return ActionResponse.<T>builder()
                .ok(true)
                .message("Success")
                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .data(null)
                .build();
    }

    public static <T> ActionResponse<T> failure(String msg) {
        return ActionResponse.<T>builder()
                .ok(false)
                .message(msg)
                .build();
    }
}