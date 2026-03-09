package com.project.dugoga.domain.user.application;

import com.project.dugoga.domain.user.application.dto.SignupRequestDto;
import com.project.dugoga.domain.user.application.dto.SignupResponseDto;
import com.project.dugoga.domain.user.application.dto.UserRequestDto;
import com.project.dugoga.domain.user.application.dto.UserResponseDto;
import com.project.dugoga.domain.user.application.service.UserService;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.project.dugoga.config.generator.UserFixtureGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service: User 서비스 테스트")
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("기능_테스트_회원가입에_성공한다")
    void 회원가입에_성공한다() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto(
                EMAIL, "Password123!", NAME, NICKNAME, ROLE
        );
        User savedUser = User.of(EMAIL, "encodedPassword123!", NAME, NICKNAME, ROLE);

        when(userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())).thenReturn(false);
        when(userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword123!");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(savedUser);

        // When
        SignupResponseDto result = userService.signup(requestDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("기능_테스트_회원_정보를_조회한다")
    void 회원_정보를_조회한다(){
        // Given
        when(userRepository.findByIdAndDeletedAtIsNull(ID)).thenReturn(Optional.of(generateUserFixture()));

        // When
        UserResponseDto result = userService.getMyInfo(ID);

        // Then
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getNickname()).isEqualTo(NICKNAME);
        assertThat(result.getUserRole()).isEqualTo(ROLE);
    }

    @Test
    @DisplayName("기능_테스트_회원_정보를_수정한다")
    void 회원_정보를_수정한다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("newPw123!", "NewName", "newNickname");

        when(userRepository.findByIdAndDeletedAtIsNull(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPw123!");

        // When
        UserResponseDto updatedUser = userService.updateMyInfo(ID, requestDto);

        // Then
        assertThat(updatedUser.getName()).isEqualTo(requestDto.getName());
        assertThat(updatedUser.getNickname()).isEqualTo(requestDto.getNickname());
    }

    @Test
    @DisplayName("예외_테스트_회원가입_도중_이메일이_중복되었다")
    void 회원가입_도중_이메일이_중복되었다() {
        // Given
        SignupRequestDto requestDto = new SignupRequestDto(
                EMAIL, "Password123!", NAME, NICKNAME, ROLE
        );

        when(userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.signup(requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.EXISTS_EMAIL.getDefaultMessage());
    }

    @Test
    @DisplayName("예외_테스트_회원_정보_수정_도중_닉네임이_중복되었다")
    void 회원_수정_도중_닉네임이_중복되었다(){
        // Given
        User user = generateUserFixture();
        UserRequestDto requestDto = new UserRequestDto("password123!", "newName", "newNickname");

        when(userRepository.findByIdAndDeletedAtIsNull(ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateMyInfo(ID, requestDto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.EXISTS_NICKNAME.getDefaultMessage());
    }

    @Test
    @DisplayName("예외_테스트_존재하지_않는_회원은_정보를_조회할_수_없다")
    void 존재하지_않는_회원은_정보를_조회할_수_없다() {
        // Given
        when(userRepository.findByIdAndDeletedAtIsNull(ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getMyInfo(ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getDefaultMessage());
    }
}
