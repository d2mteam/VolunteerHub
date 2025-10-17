package com.volunteerhub;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@EnableJpaRepositories
public class VolunteerHubProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteerHubProjectApplication.class, args);
    }
}
