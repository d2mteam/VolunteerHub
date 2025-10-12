package com.volunteerhub.authentication.configuration;

import com.volunteerhub.authentication.entity.RoleEntity;
import com.volunteerhub.authentication.entity.UserEntity;
import com.volunteerhub.authentication.entity.UserStatus;
import com.volunteerhub.authentication.repository.RoleEntityRepository;
import com.volunteerhub.authentication.repository.UserEntityRepository;
import com.volunteerhub.authentication.service.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@Configuration
@AllArgsConstructor
public class PreConfig {
    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostConstruct
    public void init() {
//        RoleEntity adminRole = roleEntityRepository.findByName("ROLE_ADMIN")
//                .orElseGet(() -> roleEntityRepository.save(new RoleEntity(UUID.randomUUID() ,"ROLE_ADMIN")));
//        RoleEntity userRole = roleEntityRepository.findByName("ROLE_USER")
//                .orElseGet(() -> roleEntityRepository.save(new RoleEntity(UUID.randomUUID() ,"ROLE_USER")));
//
//        UserEntity admin = userEntityRepository.findByUsername("first_admin")
//                .orElseGet(() -> userEntityRepository.save(new UserEntity(UUID.randomUUID(),
//                        "first_admin",
//                        passwordEncoder.encode("password"),
//                        UserStatus.ACTIVE,
//                        adminRole)));
//
//        UserEntity user = userEntityRepository.findByUsername("first_user")
//                .orElseGet(() -> userEntityRepository.save(new UserEntity(UUID.randomUUID(),
//                        "first_user",
//                        passwordEncoder.encode("password"),
//                        UserStatus.ACTIVE,
//                        userRole)));
//
//
//        System.out.println("JWT generated token " + jwtService.generateAccessToken("first_admin", List.of("ROLE_ADMIN")));
    }
}
