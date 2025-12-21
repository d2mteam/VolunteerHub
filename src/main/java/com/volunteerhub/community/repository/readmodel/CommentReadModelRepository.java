package com.volunteerhub.community.repository.readmodel;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.readmodel.CommentReadModel;

public interface CommentReadModelRepository extends RedisDocumentRepository<CommentReadModel, Long> {
}
