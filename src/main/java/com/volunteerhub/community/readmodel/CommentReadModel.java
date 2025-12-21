package com.volunteerhub.community.readmodel;

import com.redis.om.spring.annotations.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("comment-read")
public class CommentReadModel implements Serializable {
    @Id
    private Long commentId;

    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private UserProfileSummaryView createdBy;
    private Long postId;

    @Builder.Default
    @TimeToLive
    private Duration ttl = Duration.ofMinutes(10);
}
