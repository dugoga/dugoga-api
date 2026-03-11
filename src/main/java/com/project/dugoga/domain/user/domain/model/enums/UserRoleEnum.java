package com.project.dugoga.domain.user.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    CUSTOMER("CUSTOMER", "고객"),
    OWNER("OWNER", "점주"),
    MANAGER("MANAGER", "서비스 담당자"),
    MASTER("MASTER", "최종 관리자");

    private final String role;
    private final String description;
}
