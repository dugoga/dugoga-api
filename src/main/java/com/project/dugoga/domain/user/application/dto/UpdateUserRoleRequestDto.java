package com.project.dugoga.domain.user.application.dto;

import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequestDto {
    @NotBlank(message = "회원 유형을 입력해주세요.")
    private UserRoleEnum userRole;

    @AssertTrue(message = "회원 유형은 CUSTOMER, OWNER, MANAGER, MASTER 중에서만 가능합니다.")
    public boolean isValidUserRole() {
        return userRole == UserRoleEnum.CUSTOMER
                || userRole == UserRoleEnum.OWNER
                || userRole == UserRoleEnum.MANAGER
                || userRole == UserRoleEnum.MASTER;
    }
}
