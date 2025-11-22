package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.dto.graphql.output.ActionResponse;
import com.volunteerhub.community.model.table.Comment;
import com.volunteerhub.community.model.table.Post;
import com.volunteerhub.community.model.table.UserProfile;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.ICommentService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public ActionResponse<Void> createComment(UUID userId, CreateCommentInput input) {
        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
        Post post = postRepository.getReferenceById(input.getPostId());

        Comment saved = Comment.builder()
                .commentId(idGenerator.nextId())
                .post(post)
                .createdBy(userProfile)
                .content(input.getContent())
                .build();

        commentRepository.save(saved);

        LocalDateTime now = LocalDateTime.now();
        return ActionResponse.success(
                saved.getCommentId().toString(),
                now,
                now
        );
    }

    @Override
    public ActionResponse<Void> editComment(UUID userId, EditCommentInput input) {
        Optional<Comment> optional = commentRepository.findById(input.getCommentId());
        if (optional.isEmpty()) {
            return ActionResponse.failure("Comment not found");
        }

        Comment comment = optional.get();
        comment.setContent(input.getContent());
        commentRepository.save(comment);

        return ActionResponse.success(
                comment.getCommentId().toString(),
                comment.getCreatedAt(),
                LocalDateTime.now()
        );
    }

    @Override
    public ActionResponse<Void> deleteComment(UUID userId, Long commentId) {
        boolean exists = commentRepository.existsById(commentId);
        if (!exists) {
            return ActionResponse.failure("Comment not found");
        }

        commentRepository.deleteById(commentId);
        LocalDateTime now = LocalDateTime.now();

        return ActionResponse.success(
                commentId.toString(),
                now,
                now
        );
    }
}
