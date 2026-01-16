package com.volunteerhub.configuration.security.fga;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommunityFgaModelConfig {

    @Bean
    public FgaDslParser fgaDslParser() {
        return new FgaDslParser();
    }

    @Bean
    public FgaModel communityFgaModel(FgaDslParser parser) {
        return parser.parse(CommunityFgaDsl.MODEL);
    }

    @Bean
    public FgaAuthorizationEngine fgaAuthorizationEngine(FgaModel model, FgaTupleResolver tupleResolver) {
        return new FgaAuthorizationEngine(model, tupleResolver);
    }
}
