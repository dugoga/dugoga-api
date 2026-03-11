package com.project.dugoga.domain.payment.application.service;

import com.project.dugoga.config.generator.PaymentFixtureGenerator;
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
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentFailureService paymentFailureService;

    @Mock
    private PGClient pgClient;

    @Nested
    @DisplayName("결제 승인 요청")
    class ConfirmPaymentTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();
        private final String paymentKey = "payment-key";

        @Test
        @DisplayName("성공 - 결제 승인")
        void confirmPayment_success() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);

            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 15000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));

            given(order.getId()).willReturn(orderId);
            given(order.getUser()).willReturn(user);
            given(order.getTotalAmount()).willReturn(15000);
            given(order.isCreated()).willReturn(true);

            given(paymentRepository.existsByOrder_Id(orderId)).willReturn(false);

            PGPaymentDto pgPaymentDto = mock(PGPaymentDto.class);
            given(pgPaymentDto.getStatus()).willReturn("DONE");
            given(pgClient.confirm(paymentKey, orderId, 15000)).willReturn(pgPaymentDto);

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            given(paymentRepository.save(any(Payment.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentConfirmResponseDto response = paymentService.confirmPayment(userId, dto);

            // then
            assertThat(response).isNotNull();

            then(orderRepository).should()
                    .findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId);
            then(paymentRepository).should().existsByOrder_Id(orderId);
            then(pgClient).should().confirm(paymentKey, orderId, 15000);
            then(paymentRepository).should().save(paymentCaptor.capture());
            then(paymentHistoryRepository).should().save(any(PaymentHistory.class));
            then(paymentFailureService).shouldHaveNoInteractions();

            Payment savedPayment = paymentCaptor.getValue();
            assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PAID);
            assertThat(savedPayment.getMethod()).isEqualTo(PaymentMethod.CARD);
            assertThat(savedPayment.getPaymentKey()).isEqualTo(paymentKey);

            then(order).should().updateStatus(OrderStatus.PAID);
        }

        @Test
        @DisplayName("실패 - 주문 없음")
        void confirmPayment_fail_orderNotFound() {
            // given
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 15000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getDefaultMessage());

            then(paymentRepository).shouldHaveNoInteractions();
            then(pgClient).shouldHaveNoInteractions();
            then(paymentHistoryRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 주문 상태가 결제 가능 상태가 아님")
        void confirmPayment_fail_orderNotCreated() {
            // given
            Order order = mock(Order.class);
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 15000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));
            given(order.isCreated()).willReturn(false);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_PAY_NOT_ALLOWED_STATUS.getDefaultMessage());

            then(paymentRepository).shouldHaveNoInteractions();
            then(pgClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 결제 금액 불일치")
        void confirmPayment_fail_amountMismatch() {
            // given
            Order order = mock(Order.class);
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 10000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));
            given(order.isCreated()).willReturn(true);
            given(order.getTotalAmount()).willReturn(15000);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_AMOUNT_MISMATCH.getDefaultMessage());

            then(paymentRepository).should(never()).existsByOrder_Id(any());
            then(pgClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 이미 결제 정보가 존재함")
        void confirmPayment_fail_paymentAlreadyExists() {
            // given
            Order order = mock(Order.class);
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 15000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));
            given(order.isCreated()).willReturn(true);
            given(order.getId()).willReturn(orderId);
            given(order.getTotalAmount()).willReturn(15000);
            given(paymentRepository.existsByOrder_Id(orderId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_ALREADY_EXISTS.getDefaultMessage());

            then(pgClient).shouldHaveNoInteractions();
            then(paymentHistoryRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - PG 승인 실패면 실패 이력 저장")
        void confirmPayment_fail_pgConfirmFailed() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);
            PaymentConfirmRequestDto dto = new PaymentConfirmRequestDto(orderId, paymentKey, 15000);

            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));

            given(order.getId()).willReturn(orderId);
            given(order.getUser()).willReturn(user);
            given(order.getTotalAmount()).willReturn(15000);
            given(order.isCreated()).willReturn(true);

            given(paymentRepository.existsByOrder_Id(orderId)).willReturn(false);

            PGPaymentDto pgPaymentDto = mock(PGPaymentDto.class);
            given(pgPaymentDto.getStatus()).willReturn("FAILED");
            given(pgClient.confirm(paymentKey, orderId, 15000)).willReturn(pgPaymentDto);

            // when & then
            assertThatThrownBy(() -> paymentService.confirmPayment(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_CONFIRM_FAILED.getDefaultMessage());

            then(paymentFailureService).should()
                    .saveFailure(orderId, userId, paymentKey, PaymentStatus.READY, "PG confirm failed");

            then(paymentRepository).should(never()).save(any());
            then(paymentHistoryRepository).shouldHaveNoInteractions();
            then(order).should(never()).updateStatus(any());
        }
    }

    @Nested
    @DisplayName("결제 취소")
    class CancelPaymentTest {

        private final UUID orderId = UUID.randomUUID();
        private final Long userId = 1L;
        private final String paymentKey = UUID.randomUUID().toString();

        @Test
        @DisplayName("성공 - 결제 취소")
        void cancelPayment_success() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    paymentKey,
                    PaymentStatus.PAID
            );

            PaymentStatus previousStatus = payment.getStatus();

            PGPaymentDto pgPaymentDto = mock(PGPaymentDto.class);
            given(pgPaymentDto.getStatus()).willReturn("CANCELED");

            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.of(payment));
            given(pgClient.cancel(paymentKey, "사용자 주문 취소"))
                    .willReturn(pgPaymentDto);

            // when
            paymentService.cancelPayment(orderId);

            // then
            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);

            then(paymentRepository).should().findByOrder_Id(orderId);
            then(pgClient).should().cancel(paymentKey, "사용자 주문 취소");
            then(paymentHistoryRepository).should().save(any(PaymentHistory.class));
            then(paymentFailureService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 결제 정보 없음")
        void cancelPayment_fail_paymentNotFound() {
            // given
            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getDefaultMessage());

            then(pgClient).shouldHaveNoInteractions();
            then(paymentHistoryRepository).shouldHaveNoInteractions();
            then(paymentFailureService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - paymentKey 없음")
        void cancelPayment_fail_paymentKeyNotFound() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    null,
                    PaymentStatus.PAID
            );

            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.of(payment));

            // when & then
            assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_KEY_NOT_FOUND.getDefaultMessage());

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
            then(pgClient).shouldHaveNoInteractions();
            then(paymentHistoryRepository).shouldHaveNoInteractions();
            then(paymentFailureService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 취소 불가 상태")
        void cancelPayment_fail_notCancelableStatus() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    paymentKey,
                    PaymentStatus.CANCELLED
            );

            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.of(payment));

            // when & then
            assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                    .isInstanceOf(BusinessException.class);

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
            then(pgClient).shouldHaveNoInteractions();
            then(paymentHistoryRepository).shouldHaveNoInteractions();
            then(paymentFailureService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - PG 취소 결과가 CANCELED가 아니면 실패 이력 저장")
        void cancelPayment_fail_pgCancelFailed() {
            // given
            User user = mock(User.class);
            given(user.getId()).willReturn(userId);
            Order order = mock(Order.class);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    paymentKey,
                    PaymentStatus.PAID
            );

            PaymentStatus previousStatus = payment.getStatus();

            PGPaymentDto pgPaymentDto = mock(PGPaymentDto.class);
            given(pgPaymentDto.getStatus()).willReturn("FAILED");

            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.of(payment));
            given(pgClient.cancel(paymentKey, "사용자 주문 취소"))
                    .willReturn(pgPaymentDto);

            // when & then
            assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_CANCEL_FAILED.getDefaultMessage());

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);

            then(paymentFailureService).should().saveFailure(
                    orderId,
                    userId,
                    paymentKey,
                    previousStatus,
                    "PG cancel failed"
            );
            then(paymentHistoryRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - PGClient 호출 중 예외 발생")
        void cancelPayment_fail_pgClientException() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    paymentKey,
                    PaymentStatus.PAID
            );

            given(paymentRepository.findByOrder_Id(orderId))
                    .willReturn(Optional.of(payment));
            given(pgClient.cancel(paymentKey, "사용자 주문 취소"))
                    .willThrow(new RuntimeException("PG timeout"));

            // when & then
            assertThatThrownBy(() -> paymentService.cancelPayment(orderId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("PG timeout");

            assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PAID);
            then(paymentHistoryRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("결제 목록 조회")
    class SearchPaymentsTest {

        private final Long userId = 1L;
        private final UUID orderID1 = UUID.randomUUID();
        private final UUID orderID2 = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 결제 목록 조회")
        void searchPayments_success() {
            // given
            String keyword = "치킨";
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

            User user = mock(User.class);
            Order order1 = mock(Order.class);
            Order order2 = mock(Order.class);
            String paymentKey1 = UUID.randomUUID().toString();
            String paymentKey2 = UUID.randomUUID().toString();

            Payment payment1 = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order1,
                    paymentKey1,
                    PaymentStatus.PAID
            );
            Payment payment2 = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order2,
                    paymentKey2,
                    PaymentStatus.PAID
            );
            Page<Payment> page = new PageImpl<>(List.of(payment1, payment2), pageable, 2);

            given(paymentRepository.searchPayments(userId, keyword, pageable))
                    .willReturn(page);

            // when
            UserPaymentListResponseDto response = paymentService.searchPayments(userId, keyword, pageable);

            // then
            assertThat(response).isNotNull();
            then(paymentRepository).should().searchPayments(userId, keyword, pageable);
        }

        @Test
        @DisplayName("성공 - 키워드 없이 결제 목록 조회")
        void searchPayments_success_withoutKeyword() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Payment> page = new PageImpl<>(List.of(), pageable, 0);

            given(paymentRepository.searchPayments(userId, null, pageable))
                    .willReturn(page);

            // when
            UserPaymentListResponseDto response = paymentService.searchPayments(userId, null, pageable);

            // then
            assertThat(response).isNotNull();
            then(paymentRepository).should().searchPayments(userId, null, pageable);
        }
    }

    @Nested
    @DisplayName("결제 상세 조회")
    class FindPaymentTest {

        private final Long userId = 1L;
        private final UUID paymentId = UUID.randomUUID();
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 결제 상세 조회")
        void findPayment_success() {
            // given
            User user = mock(User.class);
            Order order = mock(Order.class);
            given(order.getId()).willReturn(orderId);
            String paymentKey = UUID.randomUUID().toString();
            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    paymentKey,
                    PaymentStatus.PAID
            );

            given(paymentRepository.findPayment(paymentId, userId))
                    .willReturn(Optional.of(payment));

            // when
            UserPaymentDetailResponseDto response = paymentService.findPayment(userId, paymentId);

            // then
            assertThat(response).isNotNull();
            then(paymentRepository).should().findPayment(paymentId, userId);
        }

        @Test
        @DisplayName("실패 - 결제 정보 없음")
        void findPayment_fail_paymentNotFound() {
            // given
            given(paymentRepository.findPayment(paymentId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.findPayment(userId, paymentId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getDefaultMessage());
        }
    }
}
