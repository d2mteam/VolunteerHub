package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.ultis.exception.LoginException;
import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.dto.response.RefreshResponse;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse signup(LoginRequest request) {
        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new LoginException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPasswordHash())) {
            throw new LoginException("Invalid password");
        }
        List<String> roles = List.of(userAuth.getRole().toString());

        String accessToken = jwtService.generateAccessToken(userAuth.getUserId(), roles);
        String refreshToken = jwtService.generateRefreshToken(userAuth.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshResponse refresh(String refreshToken) {
        if (refreshToken == null) {
            throw new LoginException("Missing refresh token");
        }

        String tokenType = jwtService.getTokenType(refreshToken).orElseThrow(() ->
                new LoginException("Invalid token type")
        );

        if (!jwtService.validateToken(refreshToken) ||
                !"refresh_token".equals(tokenType)) {
            throw new LoginException("Invalid refresh token");
        }

        UUID userId = jwtService.getUserIdFromToken(refreshToken).orElseThrow(() ->
                new LoginException("Invalid token payload")
        );

        UserAuth userAuth = userAuthRepository.findById(userId).orElseThrow(() ->
                new LoginException("User not found")
        );

        List<String> roles = List.of(userAuth.getRole().toString());

        String newAccessToken = jwtService.generateAccessToken(userAuth.getUserId(), roles);

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
