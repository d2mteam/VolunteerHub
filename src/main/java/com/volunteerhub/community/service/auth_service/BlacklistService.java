package com.volunteerhub.community.service.auth_service;

import java.util.UUID;

public interface BlacklistService {
    boolean validateUser(UUID userId);
    void addUserToBlacklist(UUID userId);
    void removeUserFromBlacklist(UUID userId);
}
