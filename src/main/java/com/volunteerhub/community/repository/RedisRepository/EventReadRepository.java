package com.volunteerhub.community.repository.RedisRepository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.read_model.EventRead;
import org.springframework.stereotype.Repository;

@Repository
public interface EventReadRepository extends RedisDocumentRepository<EventRead, String> {
}
