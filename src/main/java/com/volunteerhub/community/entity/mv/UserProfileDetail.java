package com.volunteerhub.community.entity.mv;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "user_profile_detail_mv")
public class UserProfileDetail {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Size(max = 100)
    @Column(name = "username", length = 100)
    private String username;

    @Size(max = 50)
    @Column(name = "full_name", length = 50)
    private String fullName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Size(max = 255)
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "status")
    private String status;

    @Column(name = "post_count")
    private Long postCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "event_count")
    private Long eventCount;

    @Column(name = "like_count")
    private Long likeCount;
}