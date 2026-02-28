package com.project.dugoga.global.security.jwt;

import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final RedisTemplate redisTemplate;

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${spring.data.redis.cache-access-token}")
    private String ACCESS_TOKEN;
    @Value("${jwt.token.expiration.access-token}")
    private long ACCESS_TOKEN_TIME;

    @Value("${spring.data.redis.cache-refresh-token}")
    private String REFRESH_TOKEN;
    @Value("${jwt.token.expiration.refresh-token}")
    private long REFRESH_TOKEN_TIME;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] stringToByte = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = Base64.getEncoder().encode(stringToByte);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access Token 발급
    public String createAccessToken(Long userId, UserRoleEnum userRole) {
        Date now = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", userRole.name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_TIME)) // 발급일로부터 30분
                .signWith(key)
                .compact();
    }

    // Refresh Token 발급
    public String createRefreshToken(Long userId) {
        Date now = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_TIME)) // 발급일로부터 14일
                .signWith(key)
                .compact();
    }

    // "Bearer <토큰>" 형식에서 토큰만 추출
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new IllegalStateException("JWT 토큰이 필요합니다.");
    }

    // JWT 토큰에서 모든 클레임을 추출
    public Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(key)
                .build();

        return parser
                .parseSignedClaims(token)
                .getPayload();
    }

    // Access 토큰 유효성 검증 + 블랙리스트 확인
    public boolean validateToken(String token) {
        // 1) 토큰 파싱/서명 검증
        Claims claims = extractAllClaims(token);

        // 2) 만료 체크
        if (claims.getExpiration().before(new Date())) {
            return false;
        }

        // 3) Access 토큰임을 강제: role 클레임 필수
        if (claims.get("role") == null) {
            return false;
        }

        // 4) 블랙리스트(유저별 1개 저장 방식) 체크
        Long userId = Long.parseLong(claims.getSubject());
        String expiredToken = redisTemplate.read(ACCESS_TOKEN + ":" + userId, String.class);
        if (expiredToken != null && expiredToken.equals(token)) {
            return false;
        }

        return true;
    }

    // Refresh Token 유효성 검증
    public boolean isValidRefreshToken(String refreshToken) {
        Claims claims = extractAllClaims(refreshToken);

        if (claims.getExpiration().before(new Date())) {
            return false;
        }

        Long userId = Long.parseLong(getSubject(refreshToken));
        String saved = redisTemplate.read(REFRESH_TOKEN + ":" + userId, String.class);

        return saved != null && saved.equals(refreshToken);
    }

    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }
}
