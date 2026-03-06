package com.project.dugoga.domain.review.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull(message="상점 id는 필수 입력값입니다.")
    @JsonProperty("store-id")
    private UUID storeId;

    @NotNull(message="주문 id는 필수 입력값입니다.")
    @JsonProperty("order-id")
    private UUID orderId;

    @NotNull(message="평점은 필수 입력값입니다.")
    private int rating;

    private String content;

    @JsonProperty("image-url")
    private String imageUrl;

}
