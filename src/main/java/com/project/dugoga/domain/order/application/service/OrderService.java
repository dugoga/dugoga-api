package com.project.dugoga.domain.order.application.service;

import com.project.dugoga.domain.order.application.dto.*;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.repository.OrderProductRepository;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.dto.PageInfoDto;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        store.validateOrderable();

        Map<UUID, Integer> productQuantityMap = toProductQuantityMap(dto);
        List<Product> products = productRepository
                .findAllByStoreIdAndIdIn(dto.getStoreId(), productQuantityMap.keySet());
        products.forEach(Product::validateOrderable);

        int amount = products.stream()
                .mapToInt(p -> p.getPrice() * productQuantityMap.get(p.getId()))
                .sum();
        int deliveryFee = calculateDeliverFee();

        Order order = Order.create(user, store, dto.getRequestMessage(), amount, deliveryFee);

        List<OrderProduct> orderProducts = products.stream()
                .map(p -> OrderProduct.create(
                        order,
                        p,
                        p.getName(),
                        productQuantityMap.get(p.getId()),
                        p.getPrice()
                ))
                .toList();
        order.addOrderProducts(orderProducts);

        orderRepository.save(order);

        return OrderCreateResponseDto.from(order);
    }

    public UserOrderListResponseDto searchUserOrderList(Long userId, String q, Pageable pageable) {
        Pageable normalizePageable = normalizePageable(pageable);
        String keyword = (q == null || q.isBlank()) ? null : q.trim();

        Page<Order> orderPage = findUserOrders(keyword, userId, normalizePageable);

        Map<UUID, List<OrderProduct>> orderProductMapByOrderId = findOrderProductsMap(orderPage);


        List<UserOrderListResponseDto.OrderResponse> orders = orderPage.getContent().stream()
                .map(o -> UserOrderListResponseDto.OrderResponse.from(
                        o,
                        orderProductMapByOrderId.getOrDefault(o.getId(), List.of())
                ))
                .toList();

        return UserOrderListResponseDto.of(orders, PageInfoDto.from(orderPage));
    }

    public OwnerOrderListResponseDto searchOwnerOrderList(Long userId, UUID storeId, String q, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        store.validateOwner(userId);

        Pageable normalizePageable = normalizePageable(pageable);
        String keyword = (q == null || q.isBlank()) ? null : q.trim();

        Page<Order> orderPage = findOwnerOrders(keyword, store.getId(), normalizePageable);

        Map<UUID, List<OrderProduct>> orderProductMapByOrderId = findOrderProductsMap(orderPage);

        List<OwnerOrderListResponseDto.OrderResponse> orders = orderPage.getContent().stream()
                .map(o -> OwnerOrderListResponseDto.OrderResponse.from(
                        o,
                        orderProductMapByOrderId.getOrDefault(o.getId(), List.of())
                ))
                .toList();

        return OwnerOrderListResponseDto.of(orders, PageInfoDto.from(orderPage));
    }

    public UserOrderDetailResponseDto getOrderDetail(Long userId, UUID orderId) {
        Order order = findOrderWithStoreByIdAndUserId(orderId, userId);

        return UserOrderDetailResponseDto.from(order);
    }

    @Transactional
    public OrderCancelResponseDto cancelOrder(Long userId, UUID orderId) {
        Order order = findOrderWithStoreByIdAndUserId(orderId, userId);

        order.validateCancelable();

        // TODO: 결제 환불 로직 추가 예정

        order.cancel();

        return OrderCancelResponseDto.from(order);
    }

    @Transactional
    public OrderAcceptResponseDto acceptOrder(Long userId, UUID orderId) {
        Order order = findOrderWithStoreById(orderId);
        order.getStore().validateOwner(userId);
        order.accept();

        return OrderAcceptResponseDto.from(order);
    }

    @Transactional
    public OrderRejectResponseDto rejectOrder(Long userId, UUID orderId) {
        Order order = findOrderWithStoreById(orderId);
        order.getStore().validateOwner(userId);

        order.validateRejectable();

        // TODO: 결제 환불 로직 추가

        order.reject();

        return OrderRejectResponseDto.from(order);
    }

    private Order findOrderWithStoreByIdAndUserId(UUID orderId, Long userId) {
        return orderRepository.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private Order findOrderWithStoreById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private static Map<UUID, Integer> toProductQuantityMap(OrderCreateRequestDto dto) {
        Map<UUID, Integer> quantityByProductId = dto.getProducts().stream()
                .collect(Collectors.toMap(
                        OrderCreateRequestDto.Product::getId,
                        OrderCreateRequestDto.Product::getQuantity,
                        Integer::sum
                ));

        if (quantityByProductId.isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_PRODUCTS_REQUIRED);
        }

        return quantityByProductId;
    }

    // TODO: 배달비 계산 로직 추가 필요
    private int calculateDeliverFee() {
        return 0;
    }

    private Map<UUID, List<OrderProduct>> findOrderProductsMap(Page<Order> orderPage) {
        List<UUID> orderIds = orderPage.getContent().stream().map(Order::getId).toList();

        return orderIds.isEmpty()
                ? Map.of()
                : orderProductRepository.findAllByOrder_IdIn(orderIds).stream()
                .collect(Collectors.groupingBy(op -> op.getOrder().getId()));
    }

    private Page<Order> findUserOrders(String keyword, Long userId, Pageable normalizePageable) {
        return (keyword == null)
                ? orderRepository.findAllByUser_Id(userId, normalizePageable)
                : orderRepository.findAllByUser_IdAndStore_NameContainingIgnoreCase(userId, keyword, normalizePageable);
    }

    private Page<Order> findOwnerOrders(String keyword, UUID storeId, Pageable normalizePageable) {
        return (keyword == null)
                ? orderRepository.findAllByStore_Id(storeId, normalizePageable)
                : orderRepository.findAllByStore_IdAndStore_NameContainingIgnoreCase(storeId, keyword, normalizePageable);
    }

    private Pageable normalizePageable(Pageable pageable) {

        int page = Math.max(pageable.getPageNumber(), 0);

        int requestedSize = pageable.getPageSize();
        int size = (requestedSize == 10 || requestedSize == 30 || requestedSize == 50)
                ? requestedSize
                : 10;

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }
}
