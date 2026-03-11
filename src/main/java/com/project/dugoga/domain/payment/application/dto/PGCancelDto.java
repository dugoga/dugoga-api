package com.project.dugoga.domain.payment.application.dto;

import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PGCancelDto {
    private String transactionKey;
    private String cancelReason;
    private LocalDateTime canceledAt;
    private Integer cancelAmount;
    private String cancelStatus;
}
