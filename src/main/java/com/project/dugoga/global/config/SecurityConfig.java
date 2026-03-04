package com.project.dugoga.global.config;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.global.filter.JwtFilter;
import com.project.dugoga.global.security.handler.CustomAccessDeniedHandler;
import com.project.dugoga.global.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 비회원 허용
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/api/auth/refresh",
                                "/error",
                                "/"
                        ).permitAll()

                        // Ai 프롬프트 생성
                        .requestMatchers("/api/ai/descriptions").hasRole(UserRoleEnum.OWNER.name())
                        .requestMatchers("/api/ai/descriptions/**").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())

                        // 서비스 가능 지역
                        .requestMatchers(
                                "/api/service-areas",
                                "/api/service-areas/**"
                        ).hasAnyRole(UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())

                        // 즐겨찾기
                        .requestMatchers(
                                "/api/stores/*/bookmarks",
                                "/api/bookmarks").hasRole(UserRoleEnum.CUSTOMER.name())

                        // 카테고리
                        .requestMatchers("/api/categories/**").hasRole(UserRoleEnum.MASTER.name())
                        .requestMatchers(HttpMethod.POST, "/api/categories").hasAnyRole(UserRoleEnum.MASTER.name(), UserRoleEnum.MANAGER.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/categories").hasAnyRole(UserRoleEnum.MASTER.name(), UserRoleEnum.MANAGER.name())

                        // 주문
                        .requestMatchers(
                                "/api/orders",
                                "/api/orders/*").hasRole(UserRoleEnum.CUSTOMER.name())
                        .requestMatchers(
                                "/api/owner/orders",
                                "/api/orders/*/*").hasRole(UserRoleEnum.OWNER.name())

                        // 결제
                        .requestMatchers(
                                "/api/payments",
                                "/api/payments/**").hasRole(UserRoleEnum.CUSTOMER.name())

                        // 상품
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())

                        // 리뷰
                        .requestMatchers(
                                "/api/reviews",
                                "/api/reviews/customer").hasRole(UserRoleEnum.CUSTOMER.name())
                        .requestMatchers("/api/reviews/stores/*").hasRole(UserRoleEnum.OWNER.name())
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*").hasAnyRole(UserRoleEnum.CUSTOMER.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/reviews/*").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/*").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())

                        // 음식점
                        .requestMatchers(
                                "/api/stores",
                                "/api/stores/status").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())
                        .requestMatchers("/api/stores/visibility").hasAnyRole(UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/stores/*").hasAnyRole(UserRoleEnum.OWNER.name(), UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/stores/*").hasAnyRole(UserRoleEnum.MANAGER.name(), UserRoleEnum.MASTER.name())

                        // 나머지 인증 필요
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)   // 401 인증 실패
                        .accessDeniedHandler(accessDeniedHandler)             // 403 인가 실패
                )

                // JWT 필터 위치
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
