package com.volunteerhub.community.model.read;

import com.volunteerhub.community.model.db_enum.EventState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_activity_summary")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventActivitySummary {
    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "event_name", nullable = false, length = 200)
    private String eventName;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;

    @Column(name = "event_location", columnDefinition = "TEXT")
    private String eventLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_state", nullable = false)
    private EventState eventState;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by_id", updatable = false)
    private UUID createdById;

    @Column(name = "created_by_username", length = 100)
    private String createdByUsername;

    @Column(name = "created_by_full_name", length = 100)
    private String createdByFullName;

    @Column(name = "created_by_avatar_id")
    private String createdByAvatarId;

    @Column(name = "new_member_count", nullable = false)
    private long newMemberCount;

    @Column(name = "new_post_count", nullable = false)
    private long newPostCount;

    @Column(name = "new_comment_count", nullable = false)
    private long newCommentCount;

    @Column(name = "new_like_count", nullable = false)
    private long newLikeCount;

    @Column(name = "latest_post_at")
    private LocalDateTime latestPostAt;

    @Column(name = "latest_interaction_at")
    private LocalDateTime latestInteractionAt;

    @PrePersist
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
