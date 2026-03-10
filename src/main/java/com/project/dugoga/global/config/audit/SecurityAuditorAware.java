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
            return Optional.of(0L);
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getId());
        }

        return Optional.of(0L);
    }
}
