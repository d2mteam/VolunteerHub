package com.volunteerhub.community.repository.readmodel;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.readmodel.PostReadModel;

public interface PostReadModelRepository extends RedisDocumentRepository<PostReadModel, Long> {
}
