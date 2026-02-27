package com.project.dugoga.domain.user.domain;

import com.project.dugoga.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9@$!%*?&#]{5,15}$", message = "비밀번호 형식이 올바르지 않습니다.")
    private String password;

    @Column(nullable = false, length = 10)
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[A-Za-z가-힣]{4,10}$", message = "이름 형식이 올바르지 않습니다.")
    private String name;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{1,10}$", message = "닉네임 형식이 올바르지 않습니다.")
    private String nickname;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum userRole;

    private boolean isDeleted = false;

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

    public void softDelete() {
        this.isDeleted = true;
    }
}
