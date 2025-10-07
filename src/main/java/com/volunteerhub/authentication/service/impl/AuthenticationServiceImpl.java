package com.volunteerhub.authentication.service.impl;

import com.volunteerhub.authentication.dtos.request.LoginRequest;
import com.volunteerhub.authentication.dtos.request.RefreshRequest;
import com.volunteerhub.authentication.dtos.request.RegistrationRequest;
import com.volunteerhub.authentication.dtos.response.LoginResponse;
import com.volunteerhub.authentication.dtos.response.RefreshResponse;
import com.volunteerhub.authentication.entity.RoleEntity;
import com.volunteerhub.authentication.entity.UserEntity;
import com.volunteerhub.authentication.entity.UserStatus;
import com.volunteerhub.authentication.repository.RoleEntityRepository;
import com.volunteerhub.authentication.repository.UserEntityRepository;
import com.volunteerhub.authentication.service.AuthenticationService;
import com.volunteerhub.authentication.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RefreshResponse refresh(RefreshRequest request) {
        if (!jwtService.validateToken(request.getRefreshToken())) {
            throw new UsernameNotFoundException("Invalid token");
        }

        String username = jwtService.usernameFromToken(request.getRefreshToken());
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = userEntity.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String accessToken = jwtService.generateAccessToken(userEntity.getUsername(), roles);

        return new RefreshResponse(accessToken);

    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity userEntity = userEntityRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        List<String> roles = userEntity.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String accessToken = jwtService.generateAccessToken(userEntity.getUsername(), roles);
        String refreshToken = jwtService.generateRefreshToken(userEntity.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void register(RegistrationRequest request) {
        if (userEntityRepository.existsByUsername(request.getUsername())) {
            throw new UsernameNotFoundException("Username exist!");
        }

        RoleEntity userRole = roleEntityRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleEntityRepository.save(new RoleEntity(UUID.randomUUID(), "ROLE_USER")));

        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();

        userEntityRepository.save(userEntity);
    }
}
