package com.project.dugoga.domain.availableaddress.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailableAddressUpdateRequestDto {

    @NotBlank(message = "시/도는 필수 입력값입니다.")
    private String region1depthName;

    @NotBlank(message = "시/군/구는 필수 입력값입니다.")
    private String region2depthName;
}
