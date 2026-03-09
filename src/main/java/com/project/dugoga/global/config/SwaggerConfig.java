package com.project.dugoga.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi categoryApi() {
        return GroupedOpenApi.builder()
                .group("카테고리")
                .pathsToMatch(
                        "/api/categories/**",
                        "/api/admin/categories")
                .build();
    }

    @Bean
    public GroupedOpenApi availableAddressApi() {
        return GroupedOpenApi.builder()
                .group("서비스 지역")
                .pathsToMatch("/api/service-areas/**")
                .build();

    }
}
