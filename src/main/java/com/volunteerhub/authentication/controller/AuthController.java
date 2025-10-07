package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dtos.request.LoginRequest;
import com.volunteerhub.authentication.dtos.request.RefreshRequest;
import com.volunteerhub.authentication.dtos.request.RegistrationRequest;
import com.volunteerhub.authentication.dtos.response.LoginResponse;
import com.volunteerhub.authentication.dtos.response.RefreshResponse;
import com.volunteerhub.authentication.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PutMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authenticationService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        RefreshResponse refreshResponse = authenticationService.refresh(request);
        return ResponseEntity.ok(refreshResponse);
    }


    @PostMapping("/registration")
    public ResponseEntity<Void> authenticate(@RequestBody RegistrationRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok().build();
    }
}
