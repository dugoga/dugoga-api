package com.project.dugoga.domain.user.domain.repository;

import com.project.dugoga.domain.user.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
