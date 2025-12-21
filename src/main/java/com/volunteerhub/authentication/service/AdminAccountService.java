package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dto.request.CreateAdminRequest;
import com.volunteerhub.authentication.model.Role;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.model.UserAuthStatus;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import com.volunteerhub.authentication.ultis.exception.AdminAccountException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAccountService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAuth createAdmin(CreateAdminRequest request) {
        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new AdminAccountException("Email already exists");
        }

        UserAuth admin = UserAuth.builder()
                .userId(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .emailVerified(true)
                .status(UserAuthStatus.ACTIVE)
                .build();

        return userAuthRepository.save(admin);
    }
}
