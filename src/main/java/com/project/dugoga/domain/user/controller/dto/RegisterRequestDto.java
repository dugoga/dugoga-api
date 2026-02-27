package com.project.dugoga.domain.user.controller.dto;

import com.project.dugoga.domain.user.entity.enums.UserRoleEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email;

    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 8~15자여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$",
            message = "비밀번호는 대소문자 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank
    @Size(min = 2, max = 10, message = "이름은 2~10자여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름에는 숫자 또는 특수문자를 사용할 수 없습니다.")
    private String name;

    @NotBlank
    @Size(min = 1, max = 10, message = "별명은 1~10자여야 합니다.")
    private String nickname;

    @NotNull(message = "회원 유형은 필수입니다.")
    private UserRoleEnum userRole;

    @AssertTrue(message = "회원 유형은 CUSTOMER, OWNER, MANAGER, MASTER 중에서만 가능합니다.")
    public boolean isValidUserRole() {
        return userRole == UserRoleEnum.CUSTOMER
                || userRole == UserRoleEnum.OWNER
                || userRole == UserRoleEnum.MANAGER
                || userRole == UserRoleEnum.MASTER;
    }
}
