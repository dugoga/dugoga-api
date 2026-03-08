package com.project.dugoga.domain.user.domain.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long userId);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);

    User save(User user);
}
