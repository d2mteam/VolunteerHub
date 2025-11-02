package com.volunteerhub.community.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// cache1 userId, eventId, rolesInEvent
// cache2 eventId, targetId, targetType, targetOwner

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyContext {
    private String userId;
    private String systemRole;
    private String targetId;
    private String targetType;
    private String targetOwner;
    private String eventId;
    private String eventRole;
    private Map<String, Object> runtime;
}
