package com.volunteerhub.community.controller.query;

import com.volunteerhub.community.dto.page.OffsetPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.repository.JpaRepository.CommentRepository;
import com.volunteerhub.community.repository.JpaRepository.PostRepository;
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
    public Post getPost(@Argument long postId) {
        return postRepository.findById(postId)
                .orElse(null);
    }

    @SchemaMapping(typeName = "Post", field = "comments")
    public OffsetPage<Comment> comments(Post post, @Argument int page, @Argument int size) {
        Page<Comment> page_ = commentRepository.findByPost_PostIdOrderByCreatedAtDesc(post.getPostId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return OffsetPage.<Comment>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent())
                .build();
    }
}
