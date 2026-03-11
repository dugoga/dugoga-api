package com.project.dugoga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class TestJpaAuditingConfig {
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.of(1L);
    }
}
