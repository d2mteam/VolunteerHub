package com.volunteerhub.community.model.mv;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "member_count")
    private Long memberCount;

    @Column(name = "post_count")
    private Long postCount;

    @Column(name = "like_count")
    private Long likeCount;

}