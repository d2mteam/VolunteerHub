//package com.volunteerhub.identity.infrastructure.config.security;
//
//import com.nimbusds.jose.JOSEException;
//import com.volunteerhub.identity.infrastructure.entity.RoleEntity;
//import com.volunteerhub.identity.infrastructure.entity.UserEntity;
//import com.volunteerhub.identity.infrastructure.entity.UserStatus;
//import com.volunteerhub.identity.infrastructure.repository.RoleEntityRepository;
//import com.volunteerhub.identity.infrastructure.repository.UserEntityRepository;
//import com.volunteerhub.identity.infrastructure.config.security.jwt.JwtUtils;
//import jakarta.annotation.PostConstruct;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;
//import java.util.UUID;
//
//@Configuration
//@AllArgsConstructor
//public class TestConfig {
//
//    private final UserEntityRepository userRepository;
//    private final RoleEntityRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//
//    private final UserDetailsService userDetailsService;
//    private final JwtUtils jwtUtils;
//
//    @PostConstruct
//    public void init() {
//        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
//                .orElseGet(() -> roleRepository.save(new RoleEntity(UUID.randomUUID() ,"ROLE_ADMIN")));
//        RoleEntity userRole = roleRepository.findByName("ROLE_USER")
//                .orElseGet(() -> roleRepository.save(new RoleEntity(UUID.randomUUID() ,"ROLE_USER")));
//
//        UserEntity admin = userRepository.findByUsername("first_admin")
//                .orElseGet(() -> userRepository.save(new UserEntity(UUID.randomUUID(),
//                        "first_admin",
//                        "admin@vnu.edu.vn",
//                        passwordEncoder.encode("password"),
//                        UserStatus.ACTIVE,
//                        adminRole)));
//
//        UserEntity user = userRepository.findByUsername("first_admin")
//                .orElseGet(() -> userRepository.save(new UserEntity(UUID.randomUUID(),
//                        "first_user",
//                        "user@vnu.edu.vn",
//                        passwordEncoder.encode("password"),
//                        UserStatus.ACTIVE,
//                        userRole)));
//
//
//        try {
//            System.out.println("JWT generated token " + jwtUtils.generateToken("first_admin", List.of("ROLE_ADMIN")));
//        } catch (JOSEException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
