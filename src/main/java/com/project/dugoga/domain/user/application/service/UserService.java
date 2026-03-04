package com.project.dugoga.domain.user.application.service;

import com.project.dugoga.domain.user.application.dto.*;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import com.project.dugoga.global.infrastructure.RedisTemplate;
import com.project.dugoga.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final JwtProvider jwtProvider;

    @Value("${spring.data.redis.cache-access-token}")
    private String ACCESS_TOKEN;
    @Value("${jwt.token.expiration.access-token}")
    private long ACCESS_TOKEN_TIME;

    @Value("${spring.data.redis.cache-refresh-token}")
    private String REFRESH_TOKEN;
    @Value("${jwt.token.expiration.refresh-token}")
    private long REFRESH_TOKEN_TIME;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.EXISTS_EMAIL);
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new BusinessException(ErrorCode.EXISTS_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.of(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                requestDto.getNickname(),
                requestDto.getUserRole()
        );

        User signupUser = userRepository.save(user);
        return new SignupResponseDto(signupUser.getId());
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        redisTemplate.write(REFRESH_TOKEN + ":" + user.getId(), jwtProvider.substringToken(refreshToken),
                Duration.ofMillis(REFRESH_TOKEN_TIME));

        return LoginResponseDto.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        Long userId = Long.parseLong(jwtProvider.extractUserId(jwtProvider.substringToken(accessToken)));
        redisTemplate.write(ACCESS_TOKEN + ":" + userId, jwtProvider.substringToken(accessToken),
                Duration.ofMillis(ACCESS_TOKEN_TIME));
    }

    @Transactional
    public LoginResponseDto refresh(String requestToken) {
        String token = jwtProvider.substringToken(requestToken);

        if (!jwtProvider.isValidRefreshToken(token)) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_VALID);
        }

        Long userId = Long.parseLong(jwtProvider.extractUserId(token));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.createAccessToken(userId, user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(userId);
        redisTemplate.write(REFRESH_TOKEN + ":" + userId, jwtProvider.substringToken(refreshToken),
                Duration.ofMillis(REFRESH_TOKEN_TIME));
        ;

        return LoginResponseDto.builder()
                .id(String.valueOf(user.getId()))
                .name(user.getName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        user.withdraw(userId);
    }
}
