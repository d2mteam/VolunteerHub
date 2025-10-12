package com.volunteerhub.community.controller;

import com.volunteerhub.community.dto.output.PostDto;
import com.volunteerhub.community.dto.output.TopicDto;
import com.volunteerhub.community.dto.page.GraphQLPage;
import com.volunteerhub.community.dto.page.PageInfo;
import com.volunteerhub.community.dto.page.PageUtils;
import com.volunteerhub.community.entity.Post;
import com.volunteerhub.community.entity.Topic;
import com.volunteerhub.community.mapper.PostMapper;
import com.volunteerhub.community.mapper.TopicMapper;
import com.volunteerhub.community.repository.PostRepository;
import com.volunteerhub.community.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class TopicResolver {
    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    @QueryMapping
    public TopicDto getTopic(@Argument long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        return TopicMapper.toDto(topic);
    }

    @SchemaMapping(typeName = "Topic", field = "posts")
    public GraphQLPage<PostDto> posts(TopicDto topic, @Argument int page, @Argument int size) {
        Page<Post> page_ = postRepository.findByTopic_TopicIdOrderByCreatedAtDesc(topic.getTopicId(), PageRequest.of(page, size));

        PageInfo pageInfo = PageUtils.from(page_);

        return GraphQLPage.<PostDto>builder()
                .pageInfo(pageInfo)
                .content(page_.getContent()
                        .stream()
                        .map(PostMapper::toDto)
                        .toList())
                .build();
    }

}
