package com.volunteerhub.authentication.config;

import com.volunteerhub.authentication.model.Role;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.model.UserAuthStatus;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
    private final AdminBootstrapProperties adminBootstrapProperties;
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!adminBootstrapProperties.isConfigured()) {
            log.warn("Admin bootstrap credentials are not configured; skipping admin seeding");
            return;
        }

        String email = adminBootstrapProperties.getEmail().trim();
        String password = adminBootstrapProperties.getPassword();

        userAuthRepository.findByEmail(email).ifPresentOrElse(
                existing -> updateExistingAdmin(existing, password),
                () -> createNewAdmin(email, password)
        );
    }

    private void updateExistingAdmin(UserAuth existing, String password) {
        existing.setRole(Role.ADMIN);
        existing.setEmailVerified(true);
        existing.setStatus(UserAuthStatus.ACTIVE);
        existing.setPasswordHash(passwordEncoder.encode(password));

        userAuthRepository.save(existing);
        log.info("Updated existing admin account with bootstrap credentials for email: {}", existing.getEmail());
    }

    private void createNewAdmin(String email, String password) {
        UserAuth admin = UserAuth.builder()
                .userId(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .emailVerified(true)
                .status(UserAuthStatus.ACTIVE)
                .role(Role.ADMIN)
                .build();

        userAuthRepository.save(admin);
        log.info("Created bootstrap admin account for email: {}", email);
    }
}
