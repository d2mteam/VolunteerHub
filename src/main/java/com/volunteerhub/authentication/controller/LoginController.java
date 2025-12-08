package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.ultis.CookieUtils;
import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.dto.response.RefreshResponse;
import com.volunteerhub.authentication.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @Value("${security.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = loginService.login(request);

        log.debug("login response: {}", loginResponse);

        setRefreshCookie(response, loginResponse.getRefreshToken());
        return ResponseEntity.ok(Map.of("accessToken", loginResponse.getAccessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractCookie(request, "refresh_token");
        RefreshResponse refreshResponse = loginService.refresh(refreshToken);

        log.debug("refresh response: {}", refreshResponse);

        setRefreshCookie(response, refreshResponse.getRefreshToken());
        return ResponseEntity.ok(Map.of("accessToken", refreshResponse.getAccessToken()));
    }

    private void setRefreshCookie(HttpServletResponse res, String token) {
        Cookie c = new Cookie("refresh_token", token);
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setPath("/");
        c.setMaxAge(jwtRefreshExpirationMs / 1000);
        c.setAttribute("SameSite", "Strict");
        res.addCookie(c);
    }
}
