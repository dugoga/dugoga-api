package com.project.dugoga.global.config.audit;

import com.project.dugoga.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return Optional.of(customUserDetails.getId());
        }

        return Optional.empty();
    }
}
