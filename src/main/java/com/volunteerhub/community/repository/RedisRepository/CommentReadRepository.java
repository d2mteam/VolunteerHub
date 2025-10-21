package com.volunteerhub.community.repository.RedisRepository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.read_model.CommentRead;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReadRepository extends RedisDocumentRepository<CommentRead, String> {
}
