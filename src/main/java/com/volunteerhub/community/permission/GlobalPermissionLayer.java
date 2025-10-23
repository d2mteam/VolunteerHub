package com.volunteerhub.community.permission;

import java.util.UUID;

public class GlobalPermissionLayer implements PermissionLayer {
    @Override
    // userId, postId, {CREATE, EDIT, DELETE, CREATE_COMMENT}
    public boolean hasPermission(UUID userId, Long targetId, String targetType, String permission) {
        return false;
    }
}
