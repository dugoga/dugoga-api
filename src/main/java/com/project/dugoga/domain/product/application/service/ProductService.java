package com.project.dugoga.domain.product.application.service;

import com.project.dugoga.domain.product.application.dto.ProductCreateRequestDto;
import com.project.dugoga.domain.product.application.dto.ProductCreateResponseDto;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
