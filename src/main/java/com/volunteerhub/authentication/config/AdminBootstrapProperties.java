package com.volunteerhub.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.admin-bootstrap")
public class AdminBootstrapProperties {
    private String email;
    private String password;

    public boolean isConfigured() {
        return StringUtils.hasText(email) && StringUtils.hasText(password);
    }
}
