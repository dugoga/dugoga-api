package com.project.dugoga.domain.payment.application.dto;

import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import com.project.dugoga.global.dto.PageInfoDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserPaymentListResponseDto {

    private List<PaymentResponse> payments;
    private PageInfoDto pageInfo;

    @Getter
    @Builder
    public static class PaymentResponse {
        private UUID id;
        private UUID orderId;
        private Integer price;
        private PaymentMethod method;
        private PaymentStatus status;
        private LocalDateTime approvedAt;

        public static PaymentResponse from(Payment payment) {
            return PaymentResponse.builder()
                    .id(payment.getId())
                    .orderId(payment.getOrder().getId())
                    .price(payment.getPrice())
                    .method(payment.getMethod())
                    .status(payment.getStatus())
                    .approvedAt(payment.getCreatedAt())
                    .build();

        }
    }

    public static UserPaymentListResponseDto from(Page<Payment> payments) {
        return UserPaymentListResponseDto.builder()
                .payments(payments.map(PaymentResponse::from).toList())
                .pageInfo(PageInfoDto.from(payments))
                .build();
    }
}
