package com.project.dugoga.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime  = System.currentTimeMillis();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestId =  UUID.randomUUID().toString().substring(0,8);
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        MDC.put("request_id", requestId);
        MDC.put("method", method);
        MDC.put("uri", uri);

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = ((HttpServletResponse) response).getStatus();
            if (duration > 1000) {
                log.warn("Completed | [{}]{} | Status: {} | Duration: {}ms", method, uri, status, duration);
            } else {
                log.info("Completed | [{}]{} | Status: {} | Duration: {}ms", method, uri, status, duration);
            }
            MDC.clear();
        }

    }
}
