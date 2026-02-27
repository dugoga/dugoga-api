package com.project.dugoga.global.config.audit;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: SecurityContext에서 사용자 ID 정보 추출하여 세팅 필요
        return Optional.empty();
    }
}
