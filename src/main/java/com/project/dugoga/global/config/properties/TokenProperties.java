package com.project.dugoga.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="spring.data.redis.token")
public class TokenProperties {
    private String cacheAccessToken;
    private long accessTokenTime;

    private String cacheRefreshToken;
    private long refreshTokenTime;
}
