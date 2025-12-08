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

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponse login(LoginRequest request) {
        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new LoginException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPasswordHash())) {
            throw new LoginException("Invalid password");
        }

        List<String> roles = List.of(userAuth.getRole().toString());

        return new LoginResponse(
                jwtService.generateAccessToken(userAuth.getUserId(), roles),
                jwtService.generateRefreshToken(userAuth.getUserId())
        );
    }

    public RefreshResponse refresh(String refreshToken) {
        if (refreshToken == null) throw new LoginException("Missing refresh token");

        JwtService.DecodedToken dt;
        try {
            dt = jwtService.decodeAndValidate(refreshToken);
        } catch (ParseException e) {
            throw new LoginException("Invalid token type");
        }

        if (!"refresh_token".equals(dt.type())) {
            throw new LoginException("Invalid token type");
        }
        
        if (tokenBlacklistService.isBlacklisted(dt.jti())) {
            throw new LoginException("Refresh token reused (replay attack)");
        }

        UserAuth userAuth = userAuthRepository.findById(dt.userId())
                .orElseThrow(() -> new LoginException("User not found"));

        List<String> roles = List.of(userAuth.getRole().toString());
        
        String newAccessToken = jwtService.generateAccessToken(userAuth.getUserId(), roles);
        
        String newRefreshToken = jwtService.generateRefreshToken(userAuth.getUserId());
        
        long ttl = dt.exp().getTime() - System.currentTimeMillis();

        tokenBlacklistService.blacklist(dt.jti(), ttl);

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }
}
