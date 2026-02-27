package com.project.dugoga.domain.user.repository;

import com.project.dugoga.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
