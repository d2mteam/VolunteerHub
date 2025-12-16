package com.volunteerhub.community.service.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "counter-cache")
public class CounterCacheProperties {
    private boolean enableLikeCache = true;
    private boolean enableMemberCache = true;
    private boolean enablePostCache = true;

    private Duration likeTtl = Duration.ofMinutes(3);
    private Duration memberTtl = Duration.ofMinutes(3);
    private Duration postTtl = Duration.ofMinutes(3);

    private Duration redisTimeout = Duration.ofMillis(40);
    private int breakerFailureThreshold = 3;
    private Duration breakerOpenDuration = Duration.ofSeconds(30);

    private Duration singleFlightWindow = Duration.ofMillis(120);
}
