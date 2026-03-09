package com.project.dugoga.domain.payment.application.service;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.payment.application.dto.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;
    private final PGClient PGClient;
    private final PaymentFailureService paymentFailureService;

    @Transactional
    public PaymentConfirmResponseDto confirmPayment(Long userId, PaymentConfirmRequestDto dto) {
        Order order = orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(dto.getOrderId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validatePayable(order, dto);

        // TODO: PG 응답 method 기반으로 결제수단 매핑
        Payment payment = Payment.create(order, PaymentMethod.CARD, dto.getPaymentKey());
        PaymentStatus previousStatus = payment.getStatus();

        PGPaymentDto confirm = PGClient.confirm(
                dto.getPaymentKey(),
                order.getId(),
                order.getTotalAmount()
        );

        if (!"DONE".equals(confirm.getStatus())) {
            paymentFailureService.saveFailure(
                    order.getId(),
                    userId,
                    dto.getPaymentKey(),
                    PaymentStatus.READY,
                    "PG confirm failed"
            );

            throw new BusinessException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        payment.updateStatus(PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);

        order.updateStatus(OrderStatus.PAID);

        PaymentHistory paymentHistory = PaymentHistory.of(savedPayment, previousStatus, null);
        paymentHistoryRepository.save(paymentHistory);

        return PaymentConfirmResponseDto.from(savedPayment);
    }

    public UserPaymentListResponseDto searchPayments(Long userId, String keyword, Pageable pageable) {
        Page<Payment> payments = paymentRepository.searchPayments(userId, keyword, pageable);
        return UserPaymentListResponseDto.from(payments);
    }

    public UserPaymentDetailResponseDto findPayment(Long userId, UUID paymentId) {
        Payment payment = paymentRepository.findPayment(paymentId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return UserPaymentDetailResponseDto.from(payment);
    }

    private void validatePayable(Order order, PaymentConfirmRequestDto dto) {
        if (!order.isCreated()) {
            throw new BusinessException(ErrorCode.ORDER_PAY_NOT_ALLOWED_STATUS);
        }

        if (!order.getTotalAmount().equals(dto.getAmount())) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        if (paymentRepository.existsByOrder_Id(order.getId())) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
    }
}
