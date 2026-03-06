package com.project.dugoga.domain.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequestDto {
    @NotBlank
    @Size(min = 8, max = 15, message = "비밀번호는 8~15자여야 합니다.")
    private String password;
}
