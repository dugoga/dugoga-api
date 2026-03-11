package com.project.dugoga.domain.payment.application.service;

import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentFailureService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailure(
            UUID orderId,
            Long userId,
            String paymentKey,
            PaymentStatus previousStatus,
            String reason
    ) {
        Order order = orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Payment payment = Payment.create(order, PaymentMethod.CARD, paymentKey);
        payment.updateStatus(PaymentStatus.FAILED);

        Payment savedPayment = paymentRepository.save(payment);

        PaymentHistory history = PaymentHistory.of(savedPayment, previousStatus, reason);
        paymentHistoryRepository.save(history);
    }
}