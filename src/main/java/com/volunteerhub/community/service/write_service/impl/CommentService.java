package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.ModerationAction;
import com.volunteerhub.community.dto.ModerationResponse;
import com.volunteerhub.community.dto.ModerationResult;
import com.volunteerhub.community.dto.ModerationStatus;
import com.volunteerhub.community.dto.ModerationTargetType;
import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.model.Comment;
import com.volunteerhub.community.model.Post;
import com.volunteerhub.community.model.UserProfile;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.ICommentService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileRepository;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public ModerationResponse createComment(UUID userId, CreateCommentInput input) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Post post = postRepository.getReferenceById(input.getPostId());

        Comment saved = Comment.builder()
                .commentId(idGenerator.nextId())
                .post(post)
                .createdBy(userProfile)
                .content(input.getContent())
                .build();

        commentRepository.save(saved);

        return ModerationResponse.success(
                ModerationAction.CREATE_COMMENT,
                ModerationTargetType.COMMENT,
                saved.getCommentId().toString(),
                ModerationStatus.CREATED,
                "Comment created"
        );
    }

    @Override
    public ModerationResponse editComment(UUID userId, EditCommentInput input) {
        Optional<Comment> optional = commentRepository.findById(input.getCommentId());
        if (optional.isEmpty()) {
            return ModerationResponse.failure(
                    ModerationAction.EDIT_COMMENT,
                    ModerationTargetType.COMMENT,
                    input.getCommentId().toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "Comment not found",
                    "COMMENT_NOT_FOUND"
            );
        }

        Comment comment = optional.get();
        comment.setContent(input.getContent());
        commentRepository.save(comment);

        return ModerationResponse.success(
                ModerationAction.EDIT_COMMENT,
                ModerationTargetType.COMMENT,
                comment.getCommentId().toString(),
                ModerationStatus.UPDATED,
                "Comment updated"
        );
    }

    @Override
    public ModerationResponse deleteComment(UUID userId, Long commentId) {
        boolean exists = commentRepository.existsById(commentId);
        if (!exists) {
            return ModerationResponse.failure(
                    ModerationAction.DELETE_COMMENT,
                    ModerationTargetType.COMMENT,
                    commentId.toString(),
                    ModerationResult.NOT_FOUND,
                    ModerationStatus.FAILED,
                    "Comment not found",
                    "COMMENT_NOT_FOUND"
            );
        }

        commentRepository.deleteById(commentId);

        return ModerationResponse.success(
                ModerationAction.DELETE_COMMENT,
                ModerationTargetType.COMMENT,
                commentId.toString(),
                ModerationStatus.DELETED,
                "Comment deleted"
        );
    }
}
