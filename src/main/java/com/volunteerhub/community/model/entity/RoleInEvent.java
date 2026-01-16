package com.volunteerhub.community.model.entity;

import com.volunteerhub.community.model.db_enum.EventRole;
import com.volunteerhub.community.model.db_enum.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_in_event", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_profile_id", "event_id"})
})
@SQLDelete(sql = "UPDATE role_in_event SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "RoleInEvent.full",
        attributeNodes = {
                @NamedAttributeNode("event"),
                @NamedAttributeNode("userProfile")
        }
)
public class RoleInEvent {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", updatable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", updatable = false)
    private Event event;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "event_role", nullable = false)
    private EventRole eventRole = EventRole.EVENT_MEMBER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "participation_status", nullable = false)
    private ParticipationStatus participationStatus = ParticipationStatus.APPROVED;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
