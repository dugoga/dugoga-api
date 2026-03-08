package com.project.dugoga.domain.payment.application.service;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.payment.application.dto.PaymentConfirmRequestDto;
import com.project.dugoga.domain.payment.application.dto.PaymentConfirmResponseDto;
import com.project.dugoga.domain.payment.application.dto.PaymentGatewayConfirmResult;
import com.project.dugoga.domain.payment.application.pg.PGClient;
import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.entity.PaymentHistory;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentMethod;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import com.project.dugoga.domain.payment.domain.repository.PaymentHistoryRepository;
import com.project.dugoga.domain.payment.domain.repository.PaymentRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final PGClient PGClient;

    @Transactional
    public PaymentConfirmResponseDto confirmPayment(Long userId, PaymentConfirmRequestDto dto) {

        // 주문 조회
        Order order = orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(dto.getOrderId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 상태 검증
        if (!order.isCreated()) {
            throw new BusinessException(ErrorCode.ORDER_PAY_NOT_ALLOWED_STATUS);
        }

        // 금액 검증
        if (!order.getTotalAmount().equals(dto.getAmount())) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 중복 결제정보 검증
        if (paymentRepository.existsByOrder_Id(order.getId())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        // 결제 생성 READY
        // TODO: PG 응답 method 기반으로 결제수단 매핑
        Payment payment = Payment.create(order, PaymentMethod.CARD, dto.getPaymentKey());
        PaymentStatus previousStatus = payment.getStatus();

        // 결제 승인 API 호출
        PaymentGatewayConfirmResult confirm = PGClient.confirm(
                dto.getPaymentKey(),
                order.getId(),
                order.getTotalAmount()
        );

        if (!"DONE".equals(confirm.getStatus())) {
            throw new BusinessException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        // 결제 상태 변경 PAID
        payment.updateStatus(PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);

        // 주문 상태 변경 PAID
        order.updateStatus(OrderStatus.PAID);

        // 결제이력 생성
        PaymentHistory paymentHistory = PaymentHistory.of(savedPayment, previousStatus, null);
        paymentHistoryRepository.save(paymentHistory);

        return PaymentConfirmResponseDto.from(savedPayment);
    }
}
