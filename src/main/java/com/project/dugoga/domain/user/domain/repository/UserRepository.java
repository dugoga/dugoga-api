package com.project.dugoga.domain.user.domain.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long userId);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    Page<User> findAllByUserRoleAndDeletedAtIsNull(UserRoleEnum userRole, Pageable pageable);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);

    User save(User user);
}
