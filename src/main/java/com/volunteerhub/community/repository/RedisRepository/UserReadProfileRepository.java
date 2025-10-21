package com.volunteerhub.community.repository.RedisRepository;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.volunteerhub.community.read_model.UserProfileRead;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReadProfileRepository extends RedisDocumentRepository<UserProfileRead, String> {
}
