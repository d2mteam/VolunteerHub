package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.dto.graphql.output.UserProfileSummaryView;
import com.volunteerhub.community.model.read.PostRead;
import com.volunteerhub.community.repository.PostReadRepository;
import com.volunteerhub.community.service.metric.MetricBatchService;
import com.volunteerhub.community.service.metric.MetricKey;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;

import com.volunteerhub.community.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class PostResolver {
    private final PostReadRepository postReadRepository;
    private final CommentRepository commentRepository;
    private final MetricBatchService metricBatchService;

    @QueryMapping
    public PostRead getPost(@Argument Long postId) {
        return postReadRepository.findById(postId).orElse(null);
    }

    @QueryMapping
    public OffsetPage<PostRead> findPosts(@Argument Integer page,
                                      @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<PostRead> postPage = postReadRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<PostRead>builder()
                .content(postPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Post", field = "listComment")
    public OffsetPage<Comment> listComment(PostRead post, @Argument Integer page, @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Comment> commentPage = commentRepository.findByPost_PostId(post.getPostId(), pageable);
        PageInfo pageInfo = PageUtils.from(commentPage);
        return OffsetPage.<Comment>builder()
                .content(commentPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @BatchMapping(typeName = "Post", field = "likeCount")
    public Map<PostRead, Integer> likeCount(List<PostRead> posts) {
        return metricBatchService.loadCountMetric(MetricKey.POST_LIKE_COUNT, posts);
    }

    @SchemaMapping(typeName = "Post", field = "createBy")
    public UserProfileSummaryView createBy(PostRead post) {
        return UserProfileSummaryView.builder()
                .userId(post.getCreatedById())
                .username(post.getCreatedByUsername())
                .fullName(post.getCreatedByFullName())
                .avatarId(post.getCreatedByAvatarId())
                .build();
    }
}
