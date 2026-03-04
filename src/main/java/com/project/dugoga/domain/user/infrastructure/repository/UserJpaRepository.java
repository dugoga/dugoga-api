package com.project.dugoga.domain.user.infrastructure.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User save(User user);
}
