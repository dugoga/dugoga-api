package com.project.dugoga.domain.user.infrastructure.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmailAndIsDeletedFalse(String email){
        return userJpaRepository.findByEmailAndIsDeletedFalse(email);
    };

    @Override
    public Optional<User> findByIdAndIsDeletedFalse(Long userId){
        return userJpaRepository.findByIdAndIsDeletedFalse(userId);
    };

    @Override
    public boolean existsByEmailAndIsDeletedFalse(String email){
        return userJpaRepository.existsByEmailAndIsDeletedFalse(email);
    };

    @Override
    public boolean existsByNicknameAndIsDeletedFalse(String nickname){
        return userJpaRepository.existsByNicknameAndIsDeletedFalse(nickname);
    };

    @Override
    public User save(User user){
        return userJpaRepository.save(user);
    };
}
