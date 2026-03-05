package com.project.dugoga.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="jwt.secret.key")
public class SecretKeyProperties {
    private String secretKey;
}

