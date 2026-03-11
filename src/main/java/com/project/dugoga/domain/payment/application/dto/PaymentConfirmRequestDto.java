package com.project.dugoga.domain.payment.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentConfirmRequestDto {

    @NotNull(message = "주문ID는 필수입니다.")
    private UUID orderId;

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;

    @NotNull(message = "결제금액은 필수입니다.")
    @Min(value = 0, message = "결제금액은 0원 이상이어야 합니다.")
    private Integer amount;
}
