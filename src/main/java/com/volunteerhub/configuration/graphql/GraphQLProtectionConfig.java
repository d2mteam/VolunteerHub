package com.volunteerhub.configuration.graphql;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration

public class GraphQLProtectionConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.Json);
    }

    @Bean
    public MaxQueryDepthInstrumentation depthInstrumentation() {
        return new MaxQueryDepthInstrumentation(6);
    }
}
