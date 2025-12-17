package com.volunteerhub.community.configuration.graphql;


import com.volunteerhub.community.service.redis_service.RedisCountCacheService;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;

@Configuration
public class LikeBatchLoaderConfig {
    private final RedisCountCacheService redisCountCacheService;

    public LikeBatchLoaderConfig(BatchLoaderRegistry registry, RedisCountCacheService redisCountCacheService) {
        this.redisCountCacheService = redisCountCacheService;

        registry.forName("likeDataLoader").registerBatchLoader(null);
    }
}