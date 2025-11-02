package com.volunteerhub.community.service.write_service.impl;

import com.volunteerhub.community.dto.graphql.input.CreateCommentInput;
import com.volunteerhub.community.dto.graphql.input.EditCommentInput;
import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.service.write_service.ICommentService;
import com.volunteerhub.ultis.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserProfileRepository userProfileService;
    private final SnowflakeIdGenerator idGenerator;

    @Override
    public void createComment(UUID userId, CreateCommentInput input) {
        UserProfile userProfile = userProfileService.getReferenceById(userId);
        Post post = postRepository.getReferenceById(input.getPostId());
        commentRepository.save(Comment.builder()
                .commentId(idGenerator.nextId())
                .post(post)
                .createdBy(userProfile)
                .content(input.getContent())
                .build());
    }

    @Override
    public void editComment(UUID userId, EditCommentInput input) {
        commentRepository.findById(idGenerator.nextId()).ifPresent(comment -> {
            comment.setContent(input.getComment());
        });
    }

    @Override
    public void deleteComment(UUID userId, Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
