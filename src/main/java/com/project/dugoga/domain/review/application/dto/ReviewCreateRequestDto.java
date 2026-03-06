package com.project.dugoga.domain.review.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull
    @JsonProperty("store-id")
    private UUID storeId;

    @NotNull
    @JsonProperty("order-id")
    private UUID orderId;

    @NotNull
    private int rating;

    private String content;

    @JsonProperty("image-url")
    private String imageUrl;

}
