package com.project.dugoga.domain.category.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class CategoryCreateRequestDto {

    @NotBlank(message = "카테고리 코드는 필수입니다.")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "코드는 영문, 숫자, _만 가능합니다.")
    @Size(max = 20, message = "카테고리 코드는 20자 이하입니다.")
    private String code;

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 30, message = "카테고리 이름은 30자 이하입니다.")
    private String name;
}
