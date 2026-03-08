package com.project.dugoga.domain.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRoleRequestDto {
    @NotBlank(message = "권한을 입력해주세요.")
    private String userRole;
}
