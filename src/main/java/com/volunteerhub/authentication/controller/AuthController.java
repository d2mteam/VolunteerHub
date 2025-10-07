package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dtos.request.LoginRequest;
import com.volunteerhub.authentication.dtos.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = null;
        return ResponseEntity.ok(loginResponse);
    }
}
