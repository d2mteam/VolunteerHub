package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.output.CommentDto;
import com.volunteerhub.community.dto.output.PostDto;
import com.volunteerhub.community.dto.page.GraphQLPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.mapper.CommentMapper;
import com.volunteerhub.community.mapper.PostMapper;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PostResolver {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @QueryMapping
    public PostDto getPost(@Argument long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return PostMapper.toDto(post);
    }

    @SchemaMapping(typeName = "Post", field = "comments")
    public GraphQLPage<CommentDto> comments(PostDto post, @Argument int page, @Argument int size) {
        Page<Comment> page_ = commentRepository.findByPost_PostIdOrderByCreatedAtDesc(post.getPostId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return GraphQLPage.<CommentDto>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent()
                        .stream()
                        .map(CommentMapper::toDto)
                        .toList())
                .build();
    }
}
