package com.project.dugoga.domain.user.domain.model.entity;

import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.global.entity.BaseEntity;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Table(name = "p_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    @Builder(access = AccessLevel.PRIVATE)
    private User(
            String email,
            String password,
            String name,
            String nickname,
            UserRoleEnum userRole
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public static User of(String email, String password, String name, String nickname, UserRoleEnum role) {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .userRole(role)
                .build();
    }

    public void updateInfo(String name, String nickname, String rawPassword,
                           PasswordEncoder passwordEncoder) {
        this.name = name;
        this.nickname = nickname;

        if (rawPassword != null && !rawPassword.isBlank()) {
            this.password = passwordEncoder.encode(rawPassword);
        }
    }

    public void withdraw(Long userId) {
        softDelete(userId);
    }

    public void validateOwner() {
        if (this.userRole != UserRoleEnum.OWNER) {
            throw new BusinessException(ErrorCode.USER_NOT_OWNER);
        }
    }

    public boolean isOwner() {
        return this.userRole == UserRoleEnum.OWNER;
    }
}
