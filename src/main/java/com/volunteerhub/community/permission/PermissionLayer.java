package com.volunteerhub.community.permission;

import java.util.UUID;

public interface PermissionLayer {
     boolean hasPermission(UUID userId, Long targetId, String targetType, String permission);
}
