package com.volunteerhub.community.permission;

import java.util.UUID;

public interface Policy {
    boolean evaluate(UUID userId, String targetId, String targetType, String action);
}