package com.project.dugoga.domain.user.application.service;

import com.project.dugoga.domain.user.application.dto.*;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.config.properties.TokenProperties;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import com.project.dugoga.global.infrastructure.StringRedisTemplate;
import com.project.dugoga.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProvider jwtProvider;
    private final TokenProperties tokenProperties;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())) {
            throw new BusinessException(ErrorCode.EXISTS_EMAIL);
        }

        if (userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())) {
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
        return new SignupResponseDto(signupUser.getId(), signupUser.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        stringRedisTemplate.write(tokenProperties.getCacheRefreshToken() + ":" + user.getId(), jwtProvider.substringToken(refreshToken),
                Duration.ofMillis(tokenProperties.getExpiration().getRefreshToken()));

        return LoginResponseDto.of(
                        user.getId(),
                        user.getName(),
                        accessToken,
                        refreshToken
                );
    }

    @Transactional
    public void logout(String accessToken) {
        Long userId = Long.parseLong(jwtProvider.extractUserId(jwtProvider.substringToken(accessToken)));
        stringRedisTemplate.write(tokenProperties.getCacheAccessToken() + ":" + userId, jwtProvider.substringToken(accessToken),
                Duration.ofMillis(tokenProperties.getExpiration().getAccessToken()));
    }

    @Transactional
    public LoginResponseDto refresh(String requestToken) {
        String token = jwtProvider.substringToken(requestToken);

        if (!jwtProvider.isValidRefreshToken(token)) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_VALID);
        }

        Long userId = Long.parseLong(jwtProvider.extractUserId(token));

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.createAccessToken(userId, user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(userId);
        stringRedisTemplate.write(tokenProperties.getCacheRefreshToken() + ":" + userId, jwtProvider.substringToken(refreshToken),
                Duration.ofMillis(tokenProperties.getExpiration().getRefreshToken()));
        ;

        return LoginResponseDto.of(
                user.getId(),
                user.getName(),
                accessToken,
                refreshToken
        );
    }

    @Transactional
    public void withdraw(Long userId, WithdrawRequestDto requestDto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        user.withdraw(userId);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = findUser(userId);
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserRequestDto requestDto) {
        User user = findUser(userId);

        validateDuplicatedUser(requestDto);

        user.updateInfo(requestDto.getName(), requestDto.getNickname(), requestDto.getPassword(),
                passwordEncoder);

        return UserResponseDto.from(user);
    }

    // 유저 정보 중복 여부 검사
    private void validateDuplicatedUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByNicknameAndDeletedAtIsNull(userRequestDto.getNickname())) {
            throw new BusinessException(ErrorCode.EXISTS_NICKNAME);
        }
    }

    // ID로 유저 찾기
    private User findUser(Long userId) {
        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
