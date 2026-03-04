package com.project.dugoga.domain.order.application.service;

import com.project.dugoga.domain.order.application.dto.OrderCreateRequestDto;
import com.project.dugoga.domain.order.application.dto.OrderCreateResponseDto;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.model.entity.OrderProduct;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public OrderCreateResponseDto createOrder(Long userId, OrderCreateRequestDto dto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
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
}
