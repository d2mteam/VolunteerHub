package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.configuration.CookieUtils;
import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.dto.response.RefreshResponse;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import com.volunteerhub.authentication.service.JwtService;
import com.volunteerhub.authentication.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    private final JwtService jwtService;

    private final UserAuthRepository userAuthRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = loginService.signup(request);

        log.debug("login response: {}", loginResponse);

        Cookie cookie = new Cookie("refresh_token", loginResponse.getRefreshToken());

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400000);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("accessToken", loginResponse.getAccessToken()));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
//        String refreshToken = CookieUtils.extractCookie(request, "refreshToken");
//        if (refreshToken == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
//        }
//
//        String tokenType = jwtService.getTokenType(refreshToken).get();
//        if (!jwtService.validateToken(refreshToken) ||
//                !"refresh_token".equals(tokenType)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body("Invalid refresh token");
//
//        }
//
//        UUID userId = jwtService.getUserIdFromToken(refreshToken).orElse(null);
//        if (userId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token payload");
//        }
//
//        UserAuth userAuth = userAuthRepository.findById(userId).orElse(null);
//        if (userAuth == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        }
//
//        String role = userAuth.getRole().toString();
//
//        String newAccessToken = jwtService.generateAccessToken(userId, List.of(role));
//
//        return ResponseEntity.ok(new RefreshResponse(newAccessToken));
//    }
}
