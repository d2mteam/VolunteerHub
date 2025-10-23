package com.volunteerhub.community.controller.controller;

import com.volunteerhub.community.dto.rest.request.LoginRequest;
import com.volunteerhub.community.dto.rest.request.RefreshRequest;
import com.volunteerhub.community.dto.rest.request.RegistrationRequest;
import com.volunteerhub.community.dto.rest.response.LoginResponse;
import com.volunteerhub.community.dto.rest.response.RefreshResponse;
import com.volunteerhub.community.service.auth_service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authenticationService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse refreshResponse = authenticationService.refresh(request);
        return ResponseEntity.ok(refreshResponse);
    }


    @PostMapping("/registration")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        authenticationService.register(request);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
