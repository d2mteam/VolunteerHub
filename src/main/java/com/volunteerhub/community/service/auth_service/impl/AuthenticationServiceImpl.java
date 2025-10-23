package com.volunteerhub.community.service.auth_service.impl;

import com.volunteerhub.community.dto.rest.request.LoginRequest;
import com.volunteerhub.community.dto.rest.request.RefreshRequest;
import com.volunteerhub.community.dto.rest.request.RegistrationRequest;
import com.volunteerhub.community.dto.rest.response.LoginResponse;
import com.volunteerhub.community.dto.rest.response.RefreshResponse;
import com.volunteerhub.community.entity.RoleName;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.entity.UserStatus;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.auth_service.AuthenticationService;
import com.volunteerhub.community.service.auth_service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. Tìm user theo username
        Optional<UserProfile> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty())
            throw new RuntimeException("Invalid username or password");

        UserProfile user = userOpt.get();

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new RuntimeException("Invalid username or password");

        // 3. Sinh token
        UUID userId = user.getUserId();
        List<String> roles = List.of(user.getRole().name());
        String accessToken = jwtService.generateAccessToken(userId, roles);
        String refreshToken = jwtService.generateRefreshToken(userId);

        // 4. Trả về kết quả
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RefreshResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. Xác thực refresh token
        if (!jwtService.validateToken(refreshToken))
            throw new RuntimeException("Invalid refresh token");

        // 2. Lấy userId từ token
        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        if (userId == null)
            throw new RuntimeException("Invalid refresh token payload");

        // 3. Lấy thông tin user
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Tạo access token mới
        String newAccessToken = jwtService.generateAccessToken(userId, List.of(user.getRole().name()));

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void register(RegistrationRequest request) {
        // 1. Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already exists");

        // 2. Tạo user mới
        UserProfile user = UserProfile.builder()
                .userId(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleName.ROLE_VOLUNTEER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
    }
}
