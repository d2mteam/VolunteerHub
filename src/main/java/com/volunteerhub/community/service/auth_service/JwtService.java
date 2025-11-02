package com.volunteerhub.community.service.auth_service;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateAccessToken(UUID userId, List<String> roles);

    String generateRefreshToken(UUID userId);

    boolean validateToken(String token);

    List<String> rolesFromToken(String token);

    UUID getUserIdFromToken(String token);
}
