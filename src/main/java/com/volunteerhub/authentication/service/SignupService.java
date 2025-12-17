package com.volunteerhub.authentication.service;

import com.volunteerhub.authentication.dto.request.SignUpRequest;
import com.volunteerhub.authentication.model.Role;
import com.volunteerhub.authentication.model.UserAuth;
import com.volunteerhub.authentication.model.UserAuthStatus;
import com.volunteerhub.authentication.repository.UserAuthRepository;
import com.volunteerhub.ultis.TokenUtil;
import com.volunteerhub.authentication.ultis.exception.VerificationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {
    private final UserAuthRepository userAuthRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.verify-path}")
    private String verifyPath;

    @Value("${app.api-url}")
    private String apiUrl;

    @Value("${app.require-verify")
    private String requireVerify;

    @Transactional
    public void signup(SignUpRequest request) {
        Role role = request.isEventManager() ? Role.EVENT_MANAGER : Role.USER;

        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new VerificationException("Email already registered");
        }

        UUID newUserId = UUID.randomUUID();
        if ("enable".equals(requireVerify)) {
            UserAuth userAuth = UserAuth.builder()
                    .userId(newUserId)
                    .role(role)
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .build();

            userAuthRepository.save(userAuth);

            sendVerificationEmail(newUserId, request.getEmail());
        }


        UserAuth userAuth = UserAuth.builder()
                .userId(newUserId)
                .role(role)
                .emailVerified(true)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userAuthRepository.save(userAuth);

    }

    @Transactional
    public void verify(String rawToken) {
        String hashed = TokenUtil.sha256(rawToken);
        String key = "verify:" + hashed;

        String userIdStr = (String) redisTemplate.opsForValue().get(key);
        if (userIdStr == null) {
            throw new VerificationException("Invalid or expired token");
        }

        UUID userId = UUID.fromString(userIdStr);
        UserAuth user = userAuthRepository.findById(userId)
                .orElseThrow(() -> new VerificationException("User not found"));

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userAuthRepository.save(user);
        }

        redisTemplate.delete(key);
        redisTemplate.delete("verify_index:" + userId);
    }

    @Transactional
    public void resend(String email) {
        UserAuth user = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new VerificationException("User not found"));

        if (user.isEmailVerified()) {
            throw new VerificationException("User already verified");
        }

        sendVerificationEmail(user.getUserId(), email);
    }

    private void sendVerificationEmail(UUID userId, String email) {
        String indexKey = "verify_index:" + userId;
        String oldTokenHash = (String) redisTemplate.opsForValue().get(indexKey);

        if (oldTokenHash != null) {
            redisTemplate.delete("verify:" + oldTokenHash);
            redisTemplate.delete(indexKey);
        }

        String rawToken = TokenUtil.generateRawToken(30);
        String hashed = TokenUtil.sha256(rawToken);

        redisTemplate.opsForValue().set("verify:" + hashed, userId.toString(), Duration.ofMinutes(15));
        redisTemplate.opsForValue().set(indexKey, hashed, Duration.ofMinutes(15));

        String verifyLink = String.format("%s%s?token=%s", apiUrl, verifyPath,
                URLEncoder.encode(rawToken, StandardCharsets.UTF_8));

        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendHtmlMail(
                        email,
                        "Verify your account",
                        verifyLink
                );
            } catch (Exception e) {
                log.error("Error while sending verification email", e);
            }
        });
    }
}
