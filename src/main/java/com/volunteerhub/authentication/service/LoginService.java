package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LoginService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse signup(LoginRequest request) {
        UserAuth userAuth = userAuthRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new AuthenticationCredentialsNotFoundException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), userAuth.getPasswordHash())) {
            throw new AuthenticationCredentialsNotFoundException("Invalid password");
        }
        List<String> roles = List.of(userAuth.getRole().toString());

        String accessToken = jwtService.generateAccessToken(userAuth.getUserId(), roles);
        String refreshToken = jwtService.generateRefreshToken(userAuth.getUserId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
