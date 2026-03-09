package com.project.dugoga.domain.user.infrastructure.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import static com.project.dugoga.config.generator.UserFixtureGenerator.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository: User 레파지토리 테스트")
public class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("기능_테스트_이메일로_삭제되지_않은_회원을_조회한다")
    void 이메일로_삭제되지_않은_회원을_조회한다() {
        // Given
        User user = generateUserFixture();
        userJpaRepository.save(user);

        // When
        var result = userJpaRepository.findByEmailAndDeletedAtIsNull(EMAIL);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
        assertThat(result.get().getNickname()).isEqualTo(NICKNAME);
    }

    @Test
    @DisplayName("기능_테스트_삭제되지_않은_회원_이메일_중복여부를_확인한다")
    void 삭제되지_않은_회원_이메일_중복여부를_확인한다() {
        // Given
        User user = generateUserFixture();
        userJpaRepository.save(user);

        // When
        boolean result = userJpaRepository.existsByEmailAndDeletedAtIsNull(EMAIL);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("기능_테스트_전체_회원_조회시_삭제된_회원은_제외된다")
    void 전체_회원_조회시_삭제된_회원은_제외된다() {
        // Given
        User activeUser1 = generateUserFixture(
                "user1@example.com", PASSWORD, "홍길동", "nick1", UserRoleEnum.CUSTOMER
        );
        User activeUser2 = generateUserFixture(
                "user2@example.com", PASSWORD, "김철수", "nick2", UserRoleEnum.OWNER
        );
        User deletedUser = generateUserFixture(
                "user3@example.com", PASSWORD, "이영희", "nick3", UserRoleEnum.MANAGER
        );

        userJpaRepository.save(activeUser1);
        userJpaRepository.save(activeUser2);
        User savedDeletedUser = userJpaRepository.save(deletedUser);

        savedDeletedUser.withdraw(999L);
        userJpaRepository.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userJpaRepository.findAllByDeletedAtIsNull(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("기능_테스트_권한별_회원_조회시_삭제되지_않은_해당_권한의_회원만_조회된다")
    void 권한별_회원_조회시_삭제되지_않은_해당_권한의_회원만_조회된다() {
        // Given
        User owner1 = generateUserFixture(
                "owner1@example.com", PASSWORD, "점주1", "owner1", UserRoleEnum.OWNER
        );
        User owner2 = generateUserFixture(
                "owner2@example.com", PASSWORD, "점주2", "owner2", UserRoleEnum.OWNER
        );
        User customer = generateUserFixture(
                "customer@example.com", PASSWORD, "고객1", "customer1", UserRoleEnum.CUSTOMER
        );

        userJpaRepository.save(owner1);
        userJpaRepository.save(owner2);
        userJpaRepository.save(customer);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<User> result = userJpaRepository.findAllByUserRoleAndDeletedAtIsNull(UserRoleEnum.OWNER, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(User::getUserRole)
                .containsOnly(UserRoleEnum.OWNER);
    }
}
