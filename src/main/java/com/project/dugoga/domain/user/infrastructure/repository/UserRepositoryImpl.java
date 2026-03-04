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
    public Optional<User> findByEmail(String email){
        return userJpaRepository.findByEmail(email);
    };

    @Override
    public Optional<User> findById(Long userId){
        return userJpaRepository.findById(userId);
    };

    @Override
    public boolean existsByEmail(String email){
        return userJpaRepository.existsByEmail(email);
    };

    @Override
    public boolean existsByNickname(String nickname){
        return userJpaRepository.existsByNickname(nickname);
    };

    @Override
    public User save(User user){
        return userJpaRepository.save(user);
    };
}
