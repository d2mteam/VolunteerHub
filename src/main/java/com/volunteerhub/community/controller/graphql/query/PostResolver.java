package com.volunteerhub.community.controller.graphql.query;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.model.entity.Comment;
import com.volunteerhub.community.model.entity.Post;
import com.volunteerhub.community.model.entity.UserProfile;
import com.volunteerhub.community.repository.LikeRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import com.volunteerhub.ultis.page.OffsetPage;
import com.volunteerhub.ultis.page.PageInfo;
import com.volunteerhub.ultis.page.PageUtils;

import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
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
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserProfileRepository userProfileRepository;
    private final LikeRepository likeRepository;

    @QueryMapping
    public Post getPost(@Argument Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    @QueryMapping
    public OffsetPage<Post> findPosts(@Argument Integer page,
                                      @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Post> postPage = postRepository.findAll(pageable);
        PageInfo pageInfo = PageUtils.from(postPage);
        return OffsetPage.<Post>builder()
                .content(postPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Post", field = "listComment")
    public OffsetPage<Comment> listComment(Post Post, @Argument Integer page, @Argument Integer size) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<Comment> commentPage = commentRepository.findByPost_PostId(Post.getPostId(), pageable);
        PageInfo pageInfo = PageUtils.from(commentPage);
        return OffsetPage.<Comment>builder()
                .content(commentPage.getContent())
                .pageInfo(pageInfo)
                .build();
    }

    @SchemaMapping(typeName = "Post", field = "likeCount")
    public Integer likeCount(Post post) {
        return likeRepository.countByTargetIdAndTableType(post.getPostId(), TableType.POST);
    }

    @SchemaMapping(typeName = "Post", field = "createBy")
    public UserProfile createBy(Post post) {
        return userProfileRepository.findById(post.getCreatedBy().getUserId()).orElse(null);
    }
}
