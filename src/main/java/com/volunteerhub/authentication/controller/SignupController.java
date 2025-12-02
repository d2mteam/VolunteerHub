package com.volunteerhub.authentication.controller;

import com.volunteerhub.authentication.dto.request.SignUpRequest;
import com.volunteerhub.authentication.service.SignupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        signupService.signup(request);
        return ResponseEntity.ok(Map.of("message", "Signup successful. "
               // + "Please check your email to verify."
        ));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyGet(@RequestParam("token") String rawToken) {
        return ResponseEntity.ok(null);
    }
}
