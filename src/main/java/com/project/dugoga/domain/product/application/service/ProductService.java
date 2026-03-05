package com.project.dugoga.domain.product.application.service;

import com.project.dugoga.domain.product.application.dto.ProductCreateRequestDto;
import com.project.dugoga.domain.product.application.dto.ProductCreateResponseDto;
import com.project.dugoga.domain.product.application.dto.ProductDetailsResponseDto;
import com.project.dugoga.domain.product.application.dto.ProductPageResponseDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
        User user = userRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );

        Store store = storeRepository.findById(request.getStoreId()).orElseThrow(
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

    public ProductPageResponseDto getProductPage(String search, Pageable pageable, Long userId, UserRoleEnum userRole) {
        if (isAdminUser(userRole)) {
            return getAdminProductPage(search, pageable);
        }
        return getNormalUserProductPage(search, pageable);
    }

    // CUSTOMER, OWNER(본인X) / OWNER(본인O), MASTER, MANAGER
    public ProductDetailsResponseDto getProductDetails(UUID productId, Long userId, UserRoleEnum userRole) {
        Product product = productRepository.findByIdWithStore(productId).orElseThrow(
                () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if(!isAuthorized(product, userId, userRole)) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return ProductDetailsResponseDto.from(product, product.getStore());
    }

    public ProductPageResponseDto getAdminProductPage(String search, Pageable pageable) {
        Page<Product> productPage = (search == null)
                ? productRepository.findAll(pageable)
                : productRepository.findByNameContaining(search, pageable);
        return ProductPageResponseDto.from(productPage);
    }

    public ProductPageResponseDto getNormalUserProductPage(String search, Pageable pageable) {
        Page<Product> productPage = (search == null)
                ? productRepository.findByIsHiddenFalse(pageable)
                : productRepository.findByNameContainingAndIsHiddenFalse(search, pageable);
        return ProductPageResponseDto.from(productPage);
    }

    boolean isAdminUser(UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MASTER)
                || userRole.equals(UserRoleEnum.MANAGER);
    }

    // MANAGER, MASTER, 본인 검증
    private boolean isAuthorized(Product product, Long userId, UserRoleEnum userRole) {
        return userRole.equals(UserRoleEnum.MASTER) ||
                userRole.equals(UserRoleEnum.MANAGER) ||
                product.getStore().getUser().getId().equals(userId);
    }
}
