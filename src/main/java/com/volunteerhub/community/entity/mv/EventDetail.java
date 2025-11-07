package com.volunteerhub.community.entity.mv;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "event_detail_mv")
public class EventDetail {
    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Size(max = 200)
    @Column(name = "event_name", length = 200)
    private String eventName;

    @Column(name = "event_description", length = Integer.MAX_VALUE)
    private String eventDescription;

    @Column(name = "event_location", length = Integer.MAX_VALUE)
    private String eventLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_metadata", columnDefinition = "jsonb")
    private Map<String, Object> eventMetadata;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 100)
    @Column(name = "creator_full_name", length = 100)
    private String creatorFullName;

    @Column(name = "creator_id")
    private UUID creatorId;

    @Size(max = 100)
    @Column(name = "creator_username", length = 100)
    private String creatorUsername;

    @Size(max = 255)
    @Column(name = "creator_avatar")
    private String creatorAvatar;

    @Column(name = "member_count")
    private Long memberCount;

    @Column(name = "post_count")
    private Long postCount;

    @Column(name = "like_count")
    private Long likeCount;

}