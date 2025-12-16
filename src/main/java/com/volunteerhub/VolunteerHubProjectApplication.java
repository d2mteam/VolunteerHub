package com.volunteerhub;

import com.volunteerhub.community.service.cache.CounterCacheProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties({CounterCacheProperties.class})
public class VolunteerHubProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteerHubProjectApplication.class, args);
    }
}
