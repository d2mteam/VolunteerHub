package com.volunteerhub.authentication.service;

import java.util.List;

public interface JwtService {
    String generateAccessToken(String user, List<String> roles);
    String generateRefreshToken(String user);
    boolean validateToken(String token);
    String usernameFromToken(String token);
    List<String> rolesFromToken(String token);
}
