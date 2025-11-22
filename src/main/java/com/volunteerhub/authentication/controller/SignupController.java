package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dto.SignUpRequest;
import com.volunteerhub.authentication.service.EmailService;
import com.volunteerhub.authentication.service.SignupService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class SignupController {
    private final SignupService signupService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        signupService.signup(request);
        return ResponseEntity.ok("Signup successful. Please check your email to verify.");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyGet(@RequestParam("token") String rawToken) {
        return ResponseEntity.ok(null);
    }
}
