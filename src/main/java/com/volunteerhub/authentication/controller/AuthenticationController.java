package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dto.request.LoginRequest;
import com.volunteerhub.authentication.dto.request.RefreshRequest;
import com.volunteerhub.authentication.dto.request.RegistrationRequest;
import com.volunteerhub.authentication.dto.response.LoginResponse;
import com.volunteerhub.authentication.dto.response.RefreshResponse;
import com.volunteerhub.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
