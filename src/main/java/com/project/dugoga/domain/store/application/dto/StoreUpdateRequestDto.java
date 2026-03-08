package com.project.dugoga.domain.store.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreUpdateRequestDto {

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private UUID categoryId;

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    private String comment;

    @NotBlank(message = "가게 도로명 주소는 필수입니다.")
    private String addressName;

    @NotBlank(message = "시도 단위 지역명은 필수입니다.")
    private String region1depthName;

    @NotBlank(message = "구 단위 지역명은 필수입니다.")
    private String region2depthName;

    @NotBlank(message = "동 단위 지역명은 필수입니다.")
    private String region3depthName;

    @NotBlank(message = "상세 주소는 필수입니다.")
    private String detailAddress;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "영업 시작 시간은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;

    @NotNull(message = "영업 종료 시간은 필수입니다.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;

}
