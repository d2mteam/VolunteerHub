package com.volunteerhub.community.repository.readmodel;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.readmodel.EventReadModel;

public interface EventReadModelRepository extends RedisDocumentRepository<EventReadModel, Long> {
}
