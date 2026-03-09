package com.project.dugoga.domain.product.application.service;

import com.project.dugoga.domain.product.application.dto.*;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    // OWNER(본인), MANAGER, MASTER
    @Transactional
    public ProductCreateResponseDto createProduct(ProductCreateRequestDto request, Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );

        Store store = storeRepository.findByIdAndDeletedAtIsNull(request.getStoreId()).orElseThrow(
                () -> new BusinessException(ErrorCode.STORE_NOT_FOUND)
        );

        if (user.isOwner()) {
            store.validateOwner(userId);
        }

        Product product = Product.create(
                store,
                request.getName(),
                request.getComment(),
                request.getPrice(),
                request.getImageUrl()
        );
        Product saved = productRepository.save(product);

        return ProductCreateResponseDto.from(saved);
    }

    public ProductPageResponseDto getProductPage(String search, Pageable pageable, UserRoleEnum userRole) {
        Page<Product> productPage = productRepository.searchProduct(trim(search), isAdminUser(userRole), normalizePageable(pageable));

        return ProductPageResponseDto.from(productPage);
    }

    // CUSTOMER, OWNER(본인X) / OWNER(본인O), MASTER, MANAGER
    public ProductDetailsResponseDto getProductDetails(UUID productId, Long userId, UserRoleEnum userRole) {
        Product product = productRepository.findByIdWithStoreAndDeletedAtIsNull(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if (!isAuthorized(product, userId, userRole)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return ProductDetailsResponseDto.from(product, product.getStore());
    }

    @Transactional
    public ProductUpdateResponseDto updateProduct(ProductUpdateRequestDto request, UUID productId, Long userId, UserRoleEnum userRole) {
        Product product = productRepository.findByIdWithStoreAndDeletedAtIsNull(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if (!isAuthorized(product, userId, userRole)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_OWNER);
        }
        product.update(
                request.getName(),
                request.getComment(),
                request.getPrice(),
                request.getImageUrl());
        return ProductUpdateResponseDto.from(product);
    }

    @Transactional
    public ProductVisibilityUpdateResponseDto updateVisibility(ProductVisibilityUpdateRequestDto request, Long userId, UserRoleEnum userRole) {
        List<Product> foundProduct = productRepository.findAllByIdInWithStoreAndDeletedAtIsNull(request.getProductIds());
        Map<UUID, Product> productMap = foundProduct.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<UUID> successIds = new ArrayList<>();
        List<UUID> failIds = new ArrayList<>();

        for (UUID id : request.getProductIds()) {
            Product product = productMap.get(id);

            if (product == null || !isAuthorized(product, userId, userRole)) {
                failIds.add(id);
                continue;
            }
            product.updateIsHidden(request.getIsHidden());
            successIds.add(id);
        }
        return ProductVisibilityUpdateResponseDto.of(successIds, failIds, LocalDateTime.now());
    }

    @Transactional
    public ProductStatusUpdateResponseDto updateStatus(ProductStatusUpdateRequestDto request, Long userId, UserRoleEnum userRole) {
        List<Product> foundProduct = productRepository.findAllByIdInWithStoreAndDeletedAtIsNull(request.getProductIds());
        Map<UUID, Product> productMap = foundProduct.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<UUID> successIds = new ArrayList<>();
        List<UUID> failIds = new ArrayList<>();

        for (UUID id : request.getProductIds()) {
            Product product = productMap.get(id);

            if (product == null || !isAuthorized(product, userId, userRole)) {
                failIds.add(id);
                continue;
            }
            product.updateIsSoldOut(request.getIsSoldOut());
            successIds.add(id);
        }
        return ProductStatusUpdateResponseDto.of(successIds, failIds, LocalDateTime.now());
    }

    // OWNER(본인O), MANAGER, MASTER
    @Transactional
    public void deleteProduct(UUID productId, Long userId, UserRoleEnum userRole) {
        Product product = productRepository.findByIdWithStoreAndDeletedAtIsNull(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if (!isAuthorized(product, userId, userRole)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_OWNER);
        }
        product.delete(userId);
    }

    private boolean isAdminUser(UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MASTER)
                || userRole.equals(UserRoleEnum.MANAGER);
    }

    // MANAGER, MASTER, 본인 검증
    private boolean isAuthorized(Product product, Long userId, UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MASTER) ||
                userRole.equals(UserRoleEnum.MANAGER) ||
                product.getStore().getUser().getId().equals(userId);
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

    private String trim(String str) {
        return str == null ? null : str.trim();
    }
}
