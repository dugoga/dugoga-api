package com.project.dugoga.global.config.generator.FixtureGenerator;

import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;

public class UserFixtureGenerator {
    public static final Long ID = 1L;
    public static final String EMAIL = "test@example.com";
    public static final String PASSWORD = "password123!";
    public static final String NAME = "홍길동";
    public static final String NICKNAME = "gildong";
    public static final UserRoleEnum ROLE = UserRoleEnum.CUSTOMER;

    public static User generateUserFixture() {
        return User.of(EMAIL, PASSWORD, NAME, NICKNAME, ROLE);
    }

    public static User generateUserFixture(
            String email,
            String password,
            String name,
            String nickname,
            UserRoleEnum role
    ) {
        return User.of(email, password, name, nickname, role);
    }
}
