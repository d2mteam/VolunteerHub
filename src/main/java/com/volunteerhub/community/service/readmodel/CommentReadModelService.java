package com.volunteerhub.community.service.readmodel;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.readmodel.CommentReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.community.repository.readmodel.CommentReadModelRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentReadModelService {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserProfileRepository userProfileRepository;
    private final CommentReadModelRepository commentReadModelRepository;

    public CommentReadModel getComment(Long commentId) {
        if (commentId == null) {
            return null;
        }
        return commentReadModelRepository.findById(commentId)
                .orElseGet(() -> rebuildComment(commentId));
    }

    public OffsetPage<CommentReadModel> listByPost(Long postId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findByPost_PostId(postId, pageable);
        List<CommentReadModel> content = commentPage.getContent().stream()
                .map(this::getFromCacheOrBuild)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        PageInfo pageInfo = PageUtils.from(commentPage);
        return OffsetPage.<CommentReadModel>builder()
                .content(content)
                .pageInfo(pageInfo)
                .build();
    }

    public CommentReadModel rebuildComment(Long commentId) {
        return commentRepository.findById(commentId)
                .map(this::buildAndCache)
                .orElse(null);
    }

    private CommentReadModel getFromCacheOrBuild(Comment comment) {
        return commentReadModelRepository.findById(comment.getCommentId())
                .orElseGet(() -> buildAndCache(comment));
    }

    private CommentReadModel buildAndCache(Comment comment) {
        CommentReadModel model = buildModel(comment);
        if (model != null) {
            commentReadModelRepository.save(model);
        }
        return model;
    }

    private CommentReadModel buildModel(Comment comment) {
        if (comment == null) {
            return null;
        }
        UserProfileSummaryView createdBy = userProfileRepository.findSummaryByUserId(comment.getCreatedBy().getUserId());
        return CommentReadModel.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .postId(comment.getPost().getPostId())
                .likeCount(likeRepository.countByTargetIdAndTableType(comment.getCommentId(), TableType.COMMENT))
                .createdBy(createdBy)
                .build();
    }
}
