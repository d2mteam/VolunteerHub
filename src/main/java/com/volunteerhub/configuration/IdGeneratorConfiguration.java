package com.volunteerhub.configuration;

import com.volunteerhub.ultis.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return SnowflakeIdGenerator.builder()
                .workerIdBits(5)
                .datacenterIdBits(5)
                .sequenceBits(12)
                .workerId(0)
                .datacenterId(0)
                .startEpoch(1577836800000L)
                .build();
    }
}
