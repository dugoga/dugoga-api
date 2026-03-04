package com.project.dugoga.domain.user.domain.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    boolean existsByEmailAndIsDeletedFalse(String email);

    boolean existsByNicknameAndIsDeletedFalse(String nickname);

    User save(User user);
}
