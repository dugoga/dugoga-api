package com.project.dugoga.domain.product.application.service;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.product.application.dto.*;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ProductService productService;

    @Nested
    @DisplayName("상품 등록")
    class CreateProduct {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 상품 등록")
        void createProduct_success() {
            // given
            ProductCreateRequestDto request = createProductRequest(storeId);
            User user = userEntity(userId, UserRoleEnum.MANAGER);
            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(mock(Store.class)));
            given(productRepository.save(any(Product.class)))
                    .willReturn(mock(Product.class));

            // when
            ProductCreateResponseDto response = productService.createProduct(request, userId);

            // then
            assertThat(response).isNotNull();
            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(productRepository).should().save(any(Product.class));
        }

        @Test
        @DisplayName("실패 - 본인 가게가 아닌경우 예외 반환")
        void createProductNotOwner() {
            User user = userEntity(userId, UserRoleEnum.OWNER);
            Store store = storeEntity(storeId, user, false);
            Long otherUserId = 2L;
            User otherUser = userEntity(otherUserId, UserRoleEnum.OWNER);

            ProductCreateRequestDto request = createProductRequest(storeId);

            // given
            given(userRepository.findByIdAndDeletedAtIsNull(otherUserId))
                    .willReturn(Optional.of(otherUser));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));

            // when
            Throwable t = catchThrowable(() -> productService.createProduct(request, otherUserId));

            // then
            Assertions.assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_OWNER.getDefaultMessage());
            then(productRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class GetProductPage {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final String search = "치킨";
        private final UserRoleEnum userRole = UserRoleEnum.MANAGER;
        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("성공 - 조건에 맞는 목록을 반환")
        void getProduct_success() {
            // given
            Page<Product> productPage = new PageImpl<>(List.of(mock(Product.class)));
            given(productRepository.searchProduct(eq(search), anyBoolean(), any(Pageable.class)))
                    .willReturn(productPage);

            // when
            ProductPageResponseDto response = productService.getProductPage(search, pageable, userRole);

            assertThat(response).isNotNull();
            then(productRepository).should().searchProduct(eq(search), anyBoolean(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProductDetails {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final UUID productId = UUID.randomUUID();
        private final UserRoleEnum userRole = UserRoleEnum.CUSTOMER;

        @Test
        @DisplayName("성공 - 숨겨지지 않은 상품은 모두가 조회")
        void getProductDetails_success() {
            // given
            User owner = userEntity(userId, UserRoleEnum.OWNER);
            Store store = storeEntity(storeId, owner, false);
            Product product = productEntity(store, productId);
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.of(product));

            // when
            ProductDetailsResponseDto response = productService.getProductDetails(productId, userId, userRole);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getName()).isEqualTo("두고가 정식");
            then(productRepository).should().findByIdWithStoreAndDeletedAtIsNull(productId);
        }

        @Test
        @DisplayName("실패 - 숨김처리된 상품은 권한이 없을 경우 예외 반환")
        void getProductDetails_notOwner() {
            // given
            Long ownerId = 999L;
            User owner = userEntity(ownerId, UserRoleEnum.OWNER);
            Store store = storeEntity(storeId, owner, false);
            Product product = productEntity(store, productId);
            product.updateIsHidden(true);
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.of(product));

            // when
            Throwable t = catchThrowable(() ->
                    productService.getProductDetails(productId, userId, userRole)
            );

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getDefaultMessage());
            then(productRepository).should().findByIdWithStoreAndDeletedAtIsNull(productId);
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final UUID productId = UUID.randomUUID();
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 가게관리 권한이 있을경우 상품 수정")
        void updateProduct_success() {
            // given
            ProductUpdateRequestDto request = productUpdateRequest();
            User owner = userEntity(userId, userRole);
            Store store = storeEntity(storeId, owner, false);
            Product product = productEntity(store, productId);
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.of(product));

            // when
            ProductUpdateResponseDto response = productService.updateProduct(request, productId, userId, userRole);

            // then
            assertThat(response).isNotNull();
            assertThat(product.getName()).isEqualTo(request.getName());
            then(productRepository).should().findByIdWithStoreAndDeletedAtIsNull(productId);
        }

        @Test
        @DisplayName("실패 - 존재하지않는 상품을 수정시도하면 예외 반환")
        void updateProduct_notFound() {
            // given
            ProductUpdateRequestDto request = productUpdateRequest();
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.empty());

            // when
            Throwable t = catchThrowable(() ->
                    productService.updateProduct(request, productId, userId, userRole)
            );

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getDefaultMessage());
            then(productRepository).should().findByIdWithStoreAndDeletedAtIsNull(productId);
        }

        @Test
        @DisplayName("실패 - 본인 가게 상품이 아닌 경우 예외 반환")
        void updateProduct_notOwner() {
            // given
            ProductUpdateRequestDto request = productUpdateRequest();
            Long otherOwnerId = 999L;
            User otherOwner = userEntity(otherOwnerId, UserRoleEnum.OWNER);
            Store otherStore = storeEntity(UUID.randomUUID(), otherOwner, false);
            Product product = productEntity(otherStore, productId);
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.of(product));

            // when
            Throwable t = catchThrowable(() ->
                    productService.updateProduct(request, productId, userId, userRole)
            );

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_OWNER.getDefaultMessage());
            assertThat(product.getName()).isNotEqualTo(request.getName());
        }
    }

    @Nested
    @DisplayName("상품 일괄 숨김 처리")
    class UpdateVisibility {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final UUID productId = UUID.randomUUID();
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 상품 숨김처리에 성공하면 성공리스트, 실패하면 실패리스트로 반환")
        void updateVisibility_success() {
            // given
            UUID successId = UUID.randomUUID();
            UUID unauthorizedId = UUID.randomUUID();
            UUID missingId = UUID.randomUUID();

            ProductVisibilityUpdateRequestDto request = new ProductVisibilityUpdateRequestDto(
                    List.of(successId, unauthorizedId, missingId),
                    true
            );
            User owner = userEntity(userId, userRole);
            Long otherOwnerId = 999L;
            User otherOwner = userEntity(otherOwnerId, userRole);
            UUID otherStoreId = UUID.randomUUID();

            Store ownerStore = storeEntity(storeId, owner, false);
            Store otherStore = storeEntity(otherStoreId, otherOwner, false);

            Product successProduct = productEntity(ownerStore, successId);
            Product unauthorizedProduct = productEntity(otherStore, unauthorizedId);

            given(productRepository.findAllByIdInWithStoreAndDeletedAtIsNull(anyList()))
                    .willReturn(List.of(successProduct, unauthorizedProduct));

            // when
            ProductVisibilityUpdateResponseDto response = productService.updateVisibility(request, userId, userRole);

            // then
            assertThat(response).isNotNull();
            // 성공 리스트
            assertThat(response.getSuccessIds().contains(successId)).isEqualTo(true);
            // 실패 리스트(권한 부족, 존재하지 않음)
            assertThat(response.getFailIds().contains(unauthorizedId)).isEqualTo(true);
            assertThat(response.getFailIds().contains(missingId)).isEqualTo(true);
            // 상태 변화
            assertThat(successProduct.getIsHidden()).isTrue();
            assertThat(unauthorizedProduct.getIsHidden()).isFalse();
            then(productRepository).should().findAllByIdInWithStoreAndDeletedAtIsNull(anyList());
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProduct {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final UUID productId = UUID.randomUUID();
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 가게관리 권한이 있을경우 상품 삭제")
        void deleteProduct_success() {
            // given
            User owner = userEntity(userId, UserRoleEnum.OWNER);
            Store store = storeEntity(storeId, owner, false);
            Product product = productEntity(store, productId);
            given(productRepository.findByIdWithStoreAndDeletedAtIsNull(productId))
                    .willReturn(Optional.of(product));

            // when
            productService.deleteProduct(productId, userId, userRole);

            // then
            then(productRepository).should().findByIdWithStoreAndDeletedAtIsNull(productId);
            assertThat(product.getDeletedAt()).isNotNull();
            assertThat(product.getDeletedBy()).isEqualTo(userId);
        }
    }

    private User userEntity(Long userId, UserRoleEnum role) {
        User user = User.of("dugoga@mail.com",
                "dugoga",
                "dugoga",
                "두고가",
                role);
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    private Product productEntity(Store store, UUID productId) {
        Product product = Product.create(
                store, "두고가 정식", "두고가의 인기메뉴",
                13000, "http://image.com");
        ReflectionTestUtils.setField(product, "id", productId);
        return product;
    }

    private ProductCreateRequestDto createProductRequest(UUID storeId) {
        return new ProductCreateRequestDto(
                storeId, "두고가 정식",
                "두고가의 인기 메뉴",
                13000,
                "http://image.com"
        );
    }

    private ProductUpdateRequestDto productUpdateRequest() {
        return new ProductUpdateRequestDto(
                "수정된 두고가 정식",
                "수정된 두고가의 인기 메뉴",
                13000,
                "http://image.com"
        );
    }

    private Store storeEntity(UUID storeId, User user, boolean isHidden) {
        Store store = Store.builder()
                .user(user)
                .category(Category.of("한식", "KOREA"))
                .availableAddress(mock(AvailableAddress.class))
                .name("두고가 식당")
                .comment("신선한 재료만 사용합니다.")
                .addressName("서울시 강남구 역삼동 123-45")
                .region1depthName("서울시")
                .region2depthName("강남구")
                .region3depthName("역삼동")
                .detailAddress("123-45번지")
                .longitude(127.0)
                .latitude(37.0)
                .isHidden(isHidden)
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(22, 0))
                .status(StoreStatus.OPEN)
                .reviewCount(0L)
                .averageRating(0D)
                .build();
        ReflectionTestUtils.setField(store, "id", storeId);
        return store;
    }
}