package com.volunteerhub.community.repository.RedisRepository;

import com.volunteerhub.community.cache_model.UserProfileRead;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends CrudRepository<UserProfileRead, String> {
}
