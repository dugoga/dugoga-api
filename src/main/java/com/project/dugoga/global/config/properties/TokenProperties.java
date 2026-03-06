package com.project.dugoga.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="jwt.token")
public class TokenProperties {
    private Expiration expiration = new Expiration();
    private String cacheAccessToken;
    private String cacheRefreshToken;

    @Getter
    @Setter
    public static class Expiration{
        private Long accessToken;
        private Long refreshToken;
    }
}
