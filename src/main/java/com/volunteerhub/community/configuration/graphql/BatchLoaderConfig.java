package com.volunteerhub.community.configuration.graphql;

import com.volunteerhub.community.model.db_enum.TableType;
import com.volunteerhub.community.service.cache.CounterCacheService;
import com.volunteerhub.community.service.cache.CounterKey;
import org.dataloader.DataLoader;
import org.dataloader.MappedBatchLoaderWithContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;

@Configuration
public class BatchLoaderConfig {

    public BatchLoaderConfig(BatchLoaderRegistry registry, CounterCacheService counterCacheService) {
        MappedBatchLoaderWithContext<CounterKey, Integer> likeLoader = (keys, env) -> counterCacheService.loadLikeCounts(keys.stream().toList());
        MappedBatchLoaderWithContext<Long, Integer> memberLoader = (keys, env) -> counterCacheService.loadMemberCounts(keys.stream().toList());
        MappedBatchLoaderWithContext<Long, Integer> postLoader = (keys, env) -> counterCacheService.loadPostCounts(keys.stream().toList());

        registry.withMappedBatchLoader("likeCountLoader", likeLoader, DataLoader::new);
        registry.withMappedBatchLoader("memberCountLoader", memberLoader, DataLoader::new);
        registry.withMappedBatchLoader("postCountLoader", postLoader, DataLoader::new);
    }

    public static CounterKey likeKey(TableType type, Long id) {
        return new CounterKey(type, id);
    }
}
