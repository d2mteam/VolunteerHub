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
import java.util.UUID;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "comment_detail_mv")
public class CommentDetail {
    @Id
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "content", length = Integer.MAX_VALUE)
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "creator_id")
    private UUID creatorId;

    @Size(max = 100)
    @Column(name = "creator_username", length = 100)
    private String creatorUsername;

    @Size(max = 100)
    @Column(name = "creator_full_name", length = 100)
    private String creatorFullName;

    @Size(max = 255)
    @Column(name = "creator_avatar")
    private String creatorAvatar;

    @Column(name = "like_count")
    private Long likeCount;

}