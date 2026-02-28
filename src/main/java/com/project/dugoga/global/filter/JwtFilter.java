package com.project.dugoga.global.filter;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.security.jwt.CustomUserDetails;
import com.project.dugoga.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 인증 없이 통과
        if (path.equals("/api/signup") || path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response); // 토큰 검증 없이 통과
            return;
        }

        // Authorization Header가 Bearer 타입인지 검증(JWT 기반 인증 방식인지)
        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 제거
        String jwt = jwtUtil.substringToken(bearerJwt);

        // Refresh 토큰 검증
        if (path.equals("/api/refresh")) {
            if (!jwtUtil.isValidRefreshToken(jwt)) {
                throw new UnauthorizedException("유효하지 않은 Refresh 토큰입니다.");
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Access 토큰 유효성 검증 (만료/서명/블랙리스트 등)
        if (!jwtUtil.validateToken(jwt)) {
            throw new UnauthorizedException("유효하지 않은 JWT 토큰입니다.");
        }

        // 인증 객체 설정
        String subject = jwtUtil.getSubject(jwt);
        Long userId = Long.parseLong(subject);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("해당 유저가 없습니다."));

        // SecurityContextHolder에 저장하기 위한 타입으로 변환: CustomUserDetails, UsernamePasswordAuthenticationToken
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
