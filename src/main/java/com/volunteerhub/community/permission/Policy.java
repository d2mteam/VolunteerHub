package com.volunteerhub.community.permission;

public interface Policy {
    boolean evaluate(String userId, String targetId, String targetType, String action);
}