package com.volunteerhub.authentication.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(
        name = "user_auth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_email", columnNames = "email")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "password_hash", columnDefinition = "TEXT", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private UserAuthStatus status = UserAuthStatus.ACTIVE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, nullable = false)
    private Role role = Role.USER;

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;
}
