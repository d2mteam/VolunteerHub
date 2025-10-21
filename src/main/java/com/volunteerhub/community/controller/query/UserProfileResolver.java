package com.volunteerhub.community.controller.query;


import com.volunteerhub.community.dto.page.OffsetPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Event;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.repository.JpaRepository.CommentRepository;
import com.volunteerhub.community.repository.JpaRepository.PostRepository;
import com.volunteerhub.community.repository.JpaRepository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserProfileResolver {

    private final UserProfileRepository userProfileRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @QueryMapping
    public UserProfile getUserProfile(@Argument UUID userId) {
        return userProfileRepository.findById(userId)
                .orElse(null);
    }

    @SchemaMapping(typeName = "UserProfile", field = "posts")
    public OffsetPage<Post> posts(UserProfile userProfile, @Argument int page, @Argument int size) {
        Page<Post> page_ = postRepository
                .findByCreatedBy_UserIdOrderByCreatedAtDesc(userProfile.getUserId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return OffsetPage.<Post>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent())
                .build();
    }

    @SchemaMapping(typeName = "UserProfile", field = "comments")
    public OffsetPage<Comment> comments(UserProfile userProfile, @Argument int page, @Argument int size) {
        Page<Comment> page_ = commentRepository
                .findByCreatedBy_UserIdOrderByCreatedAtDesc(userProfile.getUserId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return OffsetPage.<Comment>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent())
                .build();
    }

    @SchemaMapping(typeName = "UserProfile", field = "participatedEvents")
    public OffsetPage<Event> events(UserProfile userProfile, @Argument int page, @Argument int size) {

        PageInfo pageInfo = PageInfo.builder()
                .hasNext(0)
                .hasPrevious(0)
                .totalElements(0)
                .totalPages(0)
                .page(page)
                .size(size)
                .build();

        return OffsetPage.<Event>builder()
                .pageInfo(pageInfo)
                .content(List.of())
                .build();
    }

}
