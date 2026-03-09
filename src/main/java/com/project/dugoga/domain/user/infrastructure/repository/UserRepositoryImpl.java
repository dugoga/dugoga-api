package com.project.dugoga.domain.user.infrastructure.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmailAndDeletedAtIsNull(String email){
        return userJpaRepository.findByEmailAndDeletedAtIsNull(email);
    };

    @Override
    public Optional<User> findByIdAndDeletedAtIsNull(Long userId){
        return userJpaRepository.findByIdAndDeletedAtIsNull(userId);
    };

    @Override
    public Page<User> findAllByDeletedAtIsNull(Pageable pageable){
        return userJpaRepository.findAllByDeletedAtIsNull(pageable);
    };

    @Override
    public boolean existsByEmailAndDeletedAtIsNull(String email){
        return userJpaRepository.existsByEmailAndDeletedAtIsNull(email);
    };

    @Override
    public boolean existsByNicknameAndDeletedAtIsNull(String nickname){
        return userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    };

    @Override
    public User save(User user){
        return userJpaRepository.save(user);
    };
}
