package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dto.request.SignUpRequest;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SignupService {
    private final UserAuthRepository userAuthRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignUpRequest request) {
        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        UserAuth userAuth = UserAuth.builder()
                .userId(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userAuthRepository.save(userAuth);
    }
}
