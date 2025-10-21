package com.volunteerhub.community.repository.RedisRepository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.read_model.PostRead;

public interface PostReadRepository extends RedisDocumentRepository<PostRead, String> {
}
