package com.volunteerhub.configuration;

import com.volunteerhub.community.entity.RoleName;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.entity.UserStatus;
import com.volunteerhub.community.repository.UserProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Configuration
@AllArgsConstructor
public class PreConfiguration {
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if(!userProfileRepository.existsByUsername("admin")) {
            UserProfile userProfile = UserProfile.builder()
                    .username("admin")
                    .userId(UUID.randomUUID())
                    .password(passwordEncoder.encode("password"))
                    .status(UserStatus.ACTIVE)
                    .avatarUrl(null)
                    .role(RoleName.ROLE_ADMIN)
                    .email("admin@123.vnu.vn")
                    .fullName("admin")
                    .build();
            userProfileRepository.save(userProfile);
        }
//
//
//        List<UserProfile> userProfiles = userProfileRepository.findAll();
//
//        for (UserProfile userProfile : userProfiles) {
//            String normalized = Normalizer.normalize(userProfile.getFullName(), Normalizer.Form.NFD);
//            String withoutDiacritics = normalized.replaceAll("\\p{M}", "");
//            String result = withoutDiacritics.replaceAll("\\s+", "").toLowerCase();
//            userProfile.setPassword(passwordEncoder.encode("password"));
//            userProfile.setStatus(UserStatus.ACTIVE);
//            userProfile.setUsername(result);
//            userProfile.setRole(RoleName.ROLE_VOLUNTEER);
//        }
//
//        userProfileRepository.saveAll(userProfiles);
    }
}
