package com.volunteerhub.ultis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class URLConfiguration {
    @Value("${app.public-url}")
    private String publicUrl;

    @Value("${app.api-url}")
    private String apiUrl;

    @Value("${app.verify-path}")
    private String verifyPath;
    
    @Value("${app.reset-path}")
    private String resetPath;


}
