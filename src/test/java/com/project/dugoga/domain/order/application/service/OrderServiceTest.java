package com.project.dugoga.domain.order.application.service;

import com.project.dugoga.config.generator.OrderFixtureGenerator;
import com.project.dugoga.domain.order.application.dto.*;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.model.enums.OrderStatus;
import com.project.dugoga.domain.order.domain.repository.OrderProductRepository;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.payment.application.service.PaymentService;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Nested
    @DisplayName("주문 요청")
    class CreateOrderTest {

        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 주문 등록")
        void createOrder_success() {
            // given
            UUID productId1 = UUID.randomUUID();
            UUID productId2 = UUID.randomUUID();

            OrderCreateRequestDto request = new OrderCreateRequestDto(
                    storeId,
                    List.of(
                            new OrderCreateRequestDto.Product(productId1, 2),
                            new OrderCreateRequestDto.Product(productId2, 1)
                    ),
                    "문 앞에 놔주세요"
            );

            User user = mock(User.class);
            Store store = mock(Store.class);
            Product product1 = createMockProduct(productId1, "치킨", 10000);
            Product product2 = createMockProduct(productId2, "콜라", 2000);

            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));
            given(productRepository.findAllByStoreIdAndIdInAndDeletedAtIsNull(eq(storeId), anySet()))
                    .willReturn(List.of(product1, product2));

            // when
            OrderCreateResponseDto response = orderService.createOrder(userId, request);

            // then
            assertThat(response).isNotNull();
            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(store).should().validateOrderable();
            then(productRepository).should().findAllByStoreIdAndIdInAndDeletedAtIsNull(eq(storeId), anySet());
            then(product1).should().validateOrderable();
            then(product2).should().validateOrderable();
            then(orderRepository).should().save(any(Order.class));
        }

        @Test
        @DisplayName("유저가 없으면 예외 발생")
        void createOrder_fail_userNotFound() {
            // given
            OrderCreateRequestDto dto = mock(OrderCreateRequestDto.class);

            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());

        }

        @Test
        @DisplayName("가게가 없으면 예외 발생")
        void createOrder_fail_storeNotFound() {
            // given
            User user = mock(User.class);
            OrderCreateRequestDto dto = mock(OrderCreateRequestDto.class);

            given(dto.getStoreId()).willReturn(storeId);
            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("주문 상품이 비어 있으면 예외 발생")
        void createOrder_fail_orderProductsRequired() {
            // given
            User user = mock(User.class);
            Store store = mock(Store.class);
            OrderCreateRequestDto dto = mock(OrderCreateRequestDto.class);

            given(dto.getStoreId()).willReturn(storeId);
            given(dto.getProducts()).willReturn(List.of());

            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, dto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_PRODUCTS_REQUIRED.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("사용자 주문 목록 조회")
    class SearchUserOrderListTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 키워드 없이 조회")
        void searchUserOrderList_success_withoutKeyword() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            User user = mock(User.class);
            Order order = mock(Order.class);
            Store store = mock(Store.class);
            OrderProduct orderProduct = mock(OrderProduct.class);

            Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

            given(order.getId()).willReturn(orderId);
            given(order.getUser()).willReturn(user);
            given(order.getStore()).willReturn(store);
            given(order.getCreatedAt()).willReturn(LocalDateTime.now());
            given(orderProduct.getOrder()).willReturn(order);

            given(orderRepository.findAllWithStoreByUser_IdAndDeletedAtIsNull(eq(userId), any(Pageable.class)))
                    .willReturn(orderPage);
            given(orderProductRepository.findAllByOrder_IdIn(List.of(orderId)))
                    .willReturn(List.of(orderProduct));

            // when
            UserOrderListResponseDto response = orderService.searchUserOrderList(userId, null, pageable);

            // then
            assertThat(response).isNotNull();
            then(orderRepository).should().findAllWithStoreByUser_IdAndDeletedAtIsNull(eq(userId), any(Pageable.class));
            then(orderProductRepository).should().findAllByOrder_IdIn(List.of(orderId));
        }

        @Test
        @DisplayName("성공 - 키워드로 조회")
        void searchUserOrderList_success_withKeyword() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> orderPage = new PageImpl<>(List.of(), pageable, 0);

            given(orderRepository.findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(
                    eq(userId), eq("치킨"), any(Pageable.class)
            )).willReturn(orderPage);

            // when
            UserOrderListResponseDto response = orderService.searchUserOrderList(userId, "  치킨  ", pageable);

            // then
            assertThat(response).isNotNull();
            then(orderRepository).should()
                    .findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(
                            eq(userId), eq("치킨"), any(Pageable.class)
                    );
            then(orderProductRepository).should(never()).findAllByOrder_IdIn(any());
        }
    }

    @Nested
    @DisplayName("점주 주문 목록 조회")
    class SearchOwnerOrderListTest {

        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 점주 주문 목록 조회")
        void searchOwnerOrderList_success() {
            // given
            Store store = mock(Store.class);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Order> orderPage = new PageImpl<>(List.of(), pageable, 0);

            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));
            given(store.getId()).willReturn(storeId);
            given(orderRepository.findAllWithStoreByStore_IdAndDeletedAtIsNull(eq(storeId), any(Pageable.class)))
                    .willReturn(orderPage);

            // when
            OwnerOrderListResponseDto response = orderService.searchOwnerOrderList(userId, storeId, null, pageable);

            // then
            assertThat(response).isNotNull();
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(store).should().validateOwner(userId);
            then(orderRepository).should().findAllWithStoreByStore_IdAndDeletedAtIsNull(eq(storeId), any(Pageable.class));
        }

        @Test
        @DisplayName("실패 - 가게 없음")
        void searchOwnerOrderList_fail_storeNotFound() {
            // given
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    orderService.searchOwnerOrderList(userId, storeId, null, PageRequest.of(0, 10))
            )
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("주문 상세 조회")
    class GetOrderDetailTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 주문 상세 조회")
        void getOrderDetail_success() {
            // given
            User user = mock(User.class);
            Store store = mock(Store.class);
            Order order = OrderFixtureGenerator.generateOrderFixture(user, store);

            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));

            // when
            UserOrderDetailResponseDto response = orderService.getOrderDetail(userId, orderId);

            // then
            assertThat(response).isNotNull();
            then(orderRepository).should().findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId);
        }

        @Test
        @DisplayName("실패 - 주문 없음")
        void getOrderDetail_fail_orderNotFound() {
            // given
            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getOrderDetail(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("주문 취소")
    class CancelOrderTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 주문 취소")
        void cancelOrder_success() throws Exception {
            // given
            User user = mock(User.class);
            Store store = mock(Store.class);
            Order order = OrderFixtureGenerator.generateOrderFixture(
                    user,
                    store,
                    OrderStatus.PAID,
                    LocalDateTime.now()
            );

            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));
            willDoNothing().given(paymentService).cancelPayment(orderId);

            // when
            OrderCancelResponseDto response = orderService.cancelOrder(userId, orderId);

            // then
            assertThat(response).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
            then(orderRepository).should()
                    .findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId);
            then(paymentService).should().cancelPayment(orderId);
        }

        @Test
        @DisplayName("실패 - 주문 없음")
        void cancelOrder_fail_orderNotFound() {
            // given
            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getDefaultMessage());
            then(paymentService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 취소 가능 시간 초과")
        void cancelOrder_fail_timeExpired() throws Exception {
            // given
            User user = mock(User.class);
            Store store = mock(Store.class);
            Order order = OrderFixtureGenerator.generateOrderFixture(
                    user,
                    store,
                    OrderStatus.PAID,
                    LocalDateTime.now().minusMinutes(10) // 10분 전
            );

            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_CANCEL_TIME_EXPIRED.getDefaultMessage());
            then(paymentService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 취소 불가 상태")
        void cancelOrder_fail_notAllowedStatus() throws Exception {
            // given
            Order order = Order.create(
                    mock(User.class),
                    mock(Store.class),
                    "요청사항",
                    15000,
                    0
            );
            order.updateStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findWithStoreAndOrderProductsByIdAndUser_IdAndDeletedAtIsNull(orderId, userId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_CANCEL_NOT_ALLOWED_STATUS.getDefaultMessage());
            then(paymentService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class AcceptOrderTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 주문 수락")
        void acceptOrder_success() throws Exception {
            // given
            Store store = mock(Store.class);
            Order order = Order.create(mock(User.class), store, "요청", 10000, 0);
            order.updateStatus(OrderStatus.PAID);

            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(order));

            // when
            OrderAcceptResponseDto response = orderService.acceptOrder(userId, orderId);

            // then
            assertThat(response).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            then(orderRepository).should().findWithStoreByIdAndDeletedAtIsNull(orderId);
            then(store).should().validateOwner(userId);
        }

        @Test
        @DisplayName("실패 - 주문 없음")
        void acceptOrder_fail_orderNotFound() {
            // given
            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.acceptOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 수락 불가 상태")
        void acceptOrder_fail_notAllowedStatus() throws Exception {
            // given
            Store store = mock(Store.class);
            Order order = Order.create(mock(User.class), store, "요청", 10000, 0);
            order.updateStatus(OrderStatus.CREATED);

            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.acceptOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_ACCEPT_NOT_ALLOWED_STATUS.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("주문 거절")
    class RejectOrderTest {

        private final Long userId = 1L;
        private final UUID orderId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 주문 거절")
        void rejectOrder_success() throws Exception {
            // given
            Store store = mock(Store.class);
            Order order = Order.create(mock(User.class), store, "요청", 10000, 0);
            order.updateStatus(OrderStatus.PAID);

            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(order));
            willDoNothing().given(paymentService).cancelPayment(orderId);

            // when
            OrderRejectResponseDto response = orderService.rejectOrder(userId, orderId);

            // then
            assertThat(response).isNotNull();
            assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED);
            then(orderRepository).should().findWithStoreByIdAndDeletedAtIsNull(orderId);
            then(store).should().validateOwner(userId);
            then(paymentService).should().cancelPayment(orderId);
        }

        @Test
        @DisplayName("실패 - 주문 없음")
        void rejectOrder_fail_orderNotFound() {
            // given
            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.rejectOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 거절 불가 상태")
        void rejectOrder_fail_notAllowedStatus() throws Exception {
            // given
            Store store = mock(Store.class);
            Order order = Order.create(mock(User.class), store, "요청", 10000, 0);
            order.updateStatus(OrderStatus.CREATED);

            given(orderRepository.findWithStoreByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.rejectOrder(userId, orderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_REJECT_NOT_ALLOWED_STATUS.getDefaultMessage());
        }
    }

    private Product createMockProduct(UUID id, String name, int price) {
        Product product = mock(Product.class);
        given(product.getId()).willReturn(id);
        given(product.getName()).willReturn(name);
        given(product.getPrice()).willReturn(price);
        return product;
    }
}
