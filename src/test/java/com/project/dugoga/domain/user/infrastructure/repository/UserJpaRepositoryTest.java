package com.project.dugoga.domain.user.infrastructure.repository;

import com.project.dugoga.config.config.DataJpaTestBase;
import com.project.dugoga.domain.user.domain.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.project.dugoga.config.config.generator.UserFixtureGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Repository: User 레포지토리 테스트")
public class UserJpaRepositoryTest extends DataJpaTestBase {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("기능_테스트_회원을_생성한다")
    void 회원을_생성한다() {
        // Given
        User user = generateUserFixture();

        // When
        User savedUser = userJpaRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
        assertThat(savedUser.getName()).isEqualTo(NAME);
        assertThat(savedUser.getNickname()).isEqualTo(NICKNAME);
        assertThat(savedUser.getUserRole()).isEqualTo(ROLE);

        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedBy()).isEqualTo(1L);
        assertThat(savedUser.getUpdatedBy()).isEqualTo(1L);

        assertThat(savedUser.getDeletedAt()).isNull();
        assertThat(savedUser.getDeletedBy()).isNull();
    }

    @Test
    @DisplayName("기능_테스트_ID로_삭제되지_않은_회원을_조회한다")
    void ID로_삭제되지_않은_회원을_조회한다() {
        // Given
        User user = generateUserFixture();
        User savedUser = userJpaRepository.save(user);

        // When
        var result = userJpaRepository.findByIdAndDeletedAtIsNull(savedUser.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("기능_테스트_회원이_탈퇴하면_soft_delete_처리된다")
    void 회원이_탈퇴하면_soft_delete_처리된다() {
        // Given
        User user = generateUserFixture();
        User savedUser = userJpaRepository.save(user);

        // When
        savedUser.withdraw(1L);
        userJpaRepository.flush();

        // Then
        assertThat(savedUser.getDeletedAt()).isNotNull();
        assertThat(savedUser.getDeletedBy()).isEqualTo(1L);

        assertThat(userJpaRepository.findById(savedUser.getId())).isPresent();
        assertThat(userJpaRepository.findByIdAndDeletedAtIsNull(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("기능_테스트_회원_이메일_중복여부를_확인한다")
    void 회원_이메일_중복여부를_확인한다() {
        // Given
        User user = generateUserFixture();
        userJpaRepository.save(user);

        // When
        boolean result = userJpaRepository.existsByEmailAndDeletedAtIsNull(EMAIL);

        // Then
        assertThat(result).isTrue();
    }
}
