package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.dto.graphql.page.OffsetPage;
import com.volunteerhub.community.dto.graphql.page.PageInfo;
import com.volunteerhub.community.dto.graphql.page.PageUtils;
import com.volunteerhub.community.entity.mv.CommentDetail;
import com.volunteerhub.community.entity.mv.PostDetail;
import com.volunteerhub.community.repository.mv.CommentDetailRepository;
import com.volunteerhub.community.repository.mv.PostDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PostResolver {
    private final PostDetailRepository postDetailRepository;
    private final CommentDetailRepository commentDetailRepository;

    @QueryMapping
    public PostDetail getPost(@Argument Long postId) {
        return postDetailRepository.findById(postId).orElse(null);
    }

    @SchemaMapping(typeName = "Post", field = "listComment")
    public OffsetPage<CommentDetail> listComment(PostDetail postDetail, @Argument Integer page, @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<CommentDetail> commentPage = commentDetailRepository.findByPostId(postDetail.getPostId(), pageable);
        PageInfo pageInfo = PageUtils.from(commentPage);
        return OffsetPage.<CommentDetail>builder()
                .content(commentPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }
}
