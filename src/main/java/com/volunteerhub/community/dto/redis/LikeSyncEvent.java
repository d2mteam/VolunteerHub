package com.volunteerhub.community.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeSyncEvent {
    private Long targetId;
    private String tableType;
    private UUID userId;
    private boolean isLiked;
    private OffsetDateTime timestamp;
}
