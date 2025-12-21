package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.readmodel.CommentReadModel;
import com.volunteerhub.community.readmodel.PostReadModel;
import com.volunteerhub.community.readmodel.UserProfileSummaryView;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.service.readmodel.CommentReadModelService;
import com.volunteerhub.community.service.readmodel.PostReadModelService;
import com.volunteerhub.ultis.page.OffsetPage;
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
    private final PostReadModelService postReadModelService;
    private final CommentReadModelService commentReadModelService;

    @QueryMapping
    public PostReadModel getPost(@Argument Long postId) {
        return postReadModelService.getPost(postId);
    }

    @QueryMapping
    public OffsetPage<PostReadModel> findPosts(@Argument Integer page,
                                               @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Post> postPage = postReadModelService.getPostPage(pageable);
        return postReadModelService.findPosts(postPage);
    }

    @SchemaMapping(typeName = "Post", field = "listComment")
    public OffsetPage<CommentReadModel> listComment(PostReadModel Post, @Argument Integer page, @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        return commentReadModelService.listByPost(Post.getPostId(), pageable);
    }

    @SchemaMapping(typeName = "Post", field = "likeCount")
    public Integer likeCount(PostReadModel post) {
        PostReadModel model = postReadModelService.getPost(post.getPostId());
        return model != null ? model.getLikeCount() : 0;
    }

    @SchemaMapping(typeName = "Post", field = "createBy")
    public UserProfileSummaryView createBy(PostReadModel post) {
        PostReadModel model = postReadModelService.getPost(post.getPostId());
        return model != null ? model.getCreatedBy() : null;
    }
}
