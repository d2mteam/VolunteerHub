package com.volunteerhub.community.permission.impl;

import com.volunteerhub.community.permission.Policy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RoleInEventPolicy implements Policy {
    @Override
    public boolean evaluate(UUID userId, String targetId, String targetType, String action) {
        return false;
    }
}
