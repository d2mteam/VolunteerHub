package com.volunteerhub.community.service;

import com.volunteerhub.community.dto.input.CreateTopicInput;
import com.volunteerhub.community.dto.output.TopicDto;

public interface TopicService {
    TopicDto createTopic(CreateTopicInput input);
    void deleteTopic(Long id);
}
