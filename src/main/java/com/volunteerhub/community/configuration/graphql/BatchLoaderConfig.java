package com.volunteerhub.community.configuration.graphql;


import org.dataloader.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import reactor.core.publisher.Mono;

import java.util.*;

@Configuration
public class BatchLoaderConfig {

    public BatchLoaderConfig(BatchLoaderRegistry registry) {
        registry
                .forTypePair(Long.class, Integer.class)
                .withName("commentCountLoader")
                .registerMappedBatchLoader((postIds, env) -> {
                    // postIds: Set<Long>
                    Map<Long, Integer> map = new HashMap<>();
                    for (Long id : postIds) {
                        map.put(id, -1);  // default –1 cho mỗi postId
                    }
                    return Mono.just(map);
                });

        registry
                .forTypePair(Long.class, Integer.class)
                .withName("likeCountLoader")
                .registerMappedBatchLoader((postIds, env) -> {
                    // postIds: Set<Long>
                    Map<Long, Integer> map = new HashMap<>();
                    for (Long id : postIds) {
                        map.put(id, -11312323);  // default –1 cho mỗi postId
                    }
                    return Mono.just(map);
                });
    }
}