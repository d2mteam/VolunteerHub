package com.volunteerhub.community.controller;


import com.volunteerhub.community.dto.output.CommentDto;
import com.volunteerhub.community.dto.output.PostDto;
import com.volunteerhub.community.dto.output.TopicDto;
import com.volunteerhub.community.dto.output.UserProfileDto;
import com.volunteerhub.community.dto.page.GraphQLPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.Comment;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.entity.Topic;
import com.volunteerhub.community.entity.UserProfile;
import com.volunteerhub.community.mapper.CommentMapper;
import com.volunteerhub.community.mapper.PostMapper;
import com.volunteerhub.community.mapper.TopicMapper;
import com.volunteerhub.community.mapper.UserProfileMapper;
import com.volunteerhub.community.repository.CommentRepository;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.TopicRepository;
import com.volunteerhub.community.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserProfileResolver {

    private final UserProfileRepository userProfileRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    @QueryMapping
    public UserProfileDto getUserProfile(@Argument UUID userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserProfileMapper.toDto(userProfile);
    }

    @SchemaMapping(typeName = "UserProfile", field = "posts")
    public GraphQLPage<PostDto> posts(UserProfileDto userProfile, @Argument int page, @Argument int size) {
        Page<Post> page_ = postRepository
                .findByCreatedBy_UserIdOrderByCreatedAtDesc(userProfile.getUserId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return GraphQLPage.<PostDto>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent()
                        .stream()
                        .map(PostMapper::toDto)
                        .toList())
                .build();
    }

    @SchemaMapping(typeName = "UserProfile", field = "comments")
    public GraphQLPage<CommentDto> comments(UserProfileDto userProfile, @Argument int page, @Argument int size) {
        Page<Comment> page_ = commentRepository
                .findByCreatedBy_UserIdOrderByCreatedAtDesc(userProfile.getUserId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return GraphQLPage.<CommentDto>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent()
                        .stream()
                        .map(CommentMapper::toDto)
                        .toList())
                .build();
    }

    @SchemaMapping(typeName = "UserProfile", field = "topics")
    public GraphQLPage<TopicDto> topics(UserProfileDto userProfile, @Argument int page, @Argument int size) {
        Page<Topic> page_ = topicRepository
                .findByCreatedBy_UserIdOrderByCreatedAtDesc(userProfile.getUserId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return GraphQLPage.<TopicDto>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent()
                        .stream()
                        .map(TopicMapper::toDto)
                        .toList())
                .build();
    }
}
