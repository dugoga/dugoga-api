package com.project.dugoga.domain.user.domain.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User save(User user);
}
