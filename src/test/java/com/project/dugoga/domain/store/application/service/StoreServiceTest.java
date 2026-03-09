package com.project.dugoga.domain.store.application.service;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepository;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.application.dto.*;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.model.enums.StoreStatus;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.domain.user.domain.repository.UserRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AvailableAddressRepository availableAddressRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StoreService storeService;

    @Nested
    @DisplayName("가게 등록")
    class CreateStoreTest {

        private final Long userId = 1L;
        private final UUID categoryId = UUID.randomUUID();
        private StoreCreateRequestDto request;

        @BeforeEach
        void setUp() {
            request = createStoreRequest(userId, categoryId);
        }

        @Test
        @DisplayName("성공 - 가게 등록")
        void createStore_success() {
            // given
            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(mock(User.class)));
            given(categoryRepository.findByIdAndDeletedAtIsNull(categoryId))
                    .willReturn(Optional.of(mock(Category.class)));
            given(availableAddressRepository.findByRegion1depthNameAndRegion2depthName(anyString(), anyString()))
                    .willReturn(Optional.of(mock(AvailableAddress.class)));
            given(storeRepository.save(any(Store.class)))
                    .willReturn(mock(Store.class));

            // when
            StoreCreateResponseDto response = storeService.createStore(request, userId);

            // then
            assertThat(response).isNotNull();
            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(categoryRepository).should().findByIdAndDeletedAtIsNull(categoryId);
            then(availableAddressRepository).should().findByRegion1depthNameAndRegion2depthName(anyString(), anyString());
            then(storeRepository).should().save(any(Store.class));
        }


        @Test
        @DisplayName("실패 - 서비스 지역이 아닌경우 예외 반환")
        void createStoreNotServiceArea() {
            // given
            given(userRepository.findByIdAndDeletedAtIsNull(userId))
                    .willReturn(Optional.of(mock(User.class)));
            given(categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId()))
                    .willReturn(Optional.of(mock(Category.class)));
            given(availableAddressRepository.findByRegion1depthNameAndRegion2depthName(anyString(), anyString()))
                    .willReturn(Optional.empty());

            // when
            Throwable t = catchThrowable(() -> storeService.createStore(request, userId));

            // Then
            Assertions.assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_SERVICE_AREA.getDefaultMessage());
            then(storeRepository).shouldHaveNoInteractions();
        }

        private StoreCreateRequestDto createStoreRequest(Long userId, UUID categoryId) {
            return new StoreCreateRequestDto(
                    categoryId, "dugoga", "싱싱한 재료를 사용합니다",
                    "서울시 강남구 역삼동 123-45", "서울시", "강남구", "역삼동",
                    "123-45번지", 127.0, 37.0,
                    LocalTime.of(9, 0), LocalTime.of(22, 0));
        }
    }

    @Nested
    @DisplayName("가게 상세 조회")
    class GetStoreDetailsTest {
        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();

        @Test
        @DisplayName("성공 - 숨겨지지 않은 가게는 모두가 조회")
        void getStoreDetails_success() {
            // given
            UserRoleEnum userRole = UserRoleEnum.CUSTOMER;
            User user = userEntity(userId, userRole);
            Store store = storeEntity(storeId, user, false);
            given(storeRepository.findByIdWithDetailsAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));

            // when
            StoreDetailsResponseDto response = storeService.getStoreDetails(storeId, userId, userRole);

            // then
            assertThat(response).isNotNull();
            then(storeRepository).should().findByIdWithDetailsAndDeletedAtIsNull(storeId);
        }

        @Test
        @DisplayName("성공 - 숨겨진 가게여도 본인의 가게는 조회")
        void getStoreDetails_authorizedSuccess() {
            // given
            UserRoleEnum userRole = UserRoleEnum.OWNER;
            User user = userEntity(userId, userRole);
            Store store = storeEntity(storeId, user, true);

            given(storeRepository.findByIdWithDetailsAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));

            // when
            StoreDetailsResponseDto response = storeService.getStoreDetails(storeId, userId, userRole);

            // then
            assertThat(response).isNotNull();
            then(storeRepository).should().findByIdWithDetailsAndDeletedAtIsNull(storeId);
        }


        @Test
        @DisplayName("실패 - 존재하지 않는 가게인 경우 예외 반환")
        void getStoreDetails_storeNotFound() {
            // given
            UserRoleEnum userRole = UserRoleEnum.OWNER;
            User user = userEntity(userId, userRole);
            Store store = storeEntity(storeId, user, true);
            given(storeRepository.findByIdWithDetailsAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.empty());

            // when
            Throwable t = catchThrowable(() -> storeService.getStoreDetails(storeId, userId, userRole));

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 숨겨진 가게를 권한 없는 사용자가 접근하면 예외 반환")
        void getStoreDetails_hiddenStore() {
            // given
            UserRoleEnum userRole = UserRoleEnum.OWNER;
            User user = userEntity(userId, userRole);
            Store store = storeEntity(storeId, user, true);
            Long otherUserId = 2L;

            given(storeRepository.findByIdWithDetailsAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));

            // when
            Throwable t = catchThrowable(() -> storeService.getStoreDetails(storeId, otherUserId, userRole));

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
        }

    }

    @Nested()
    @DisplayName("가게 페이지 조회")
    class GetStorePageTest {

        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;
        private final Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        private final StoreSearchCondDto cond = new StoreSearchCondDto("두고가 식당", "한식");

        @Test
        @DisplayName("성공 - 조건에 맞는 가게 목록을 반환")
        void getStorePage_success() {
            // given
            given(categoryRepository.existsByNameAndDeletedAtIsNull(anyString()))
                    .willReturn(true);
            List<Store> stores = List.of(storeEntity(UUID.randomUUID(), mock(User.class), false));
            Page<Store> storePage = new PageImpl<>(stores, pageable, stores.size());
            given(storeRepository.searchStores(
                    anyString(),
                    anyString(),
                    eq(userId),
                    anyBoolean(),
                    any(Pageable.class)))
                    .willReturn(storePage);

            // when
            StorePageResponseDto response = storeService.getStorePage(cond, userId, userRole, Pageable.ofSize(1));

            // then
            assertThat(response).isNotNull();
            then(categoryRepository).should().existsByNameAndDeletedAtIsNull(cond.getCategory());
            then(storeRepository).should().searchStores(
                    anyString(),
                    anyString(),
                    eq(userId),
                    anyBoolean(),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리 검색 시 예외 반환")
        void getStorePage_categoryNotFound() {
            // given
            given(categoryRepository.existsByNameAndDeletedAtIsNull(cond.getCategory()))
                    .willReturn(false);

            // when
            Throwable t = catchThrowable(() ->
                    storeService.getStorePage(cond, userId, userRole, pageable)
            );

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage());
            then(categoryRepository).should().existsByNameAndDeletedAtIsNull(cond.getCategory());
            then(storeRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("가게 상품 목록 조회")
    class GetStoreProductPageTest {

        private final Long userId = 1L;
        private final UUID storeId = UUID.randomUUID();
        private final String search = "치킨";
        private final UserRoleEnum userRole = UserRoleEnum.CUSTOMER;
        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("성공 - 가게가 존재하면 상품 목록을 반환")
        void getStoreProductPage_success() {
            // given
            User user = userEntity(userId, userRole);
            Store store = storeEntity(storeId, user, false);
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));
            Page<Product> productPage = new PageImpl<>(List.of(mock(Product.class)));
            given(productRepository.searchStoreProduct(eq(storeId), anyString(), anyBoolean(), any(Pageable.class)))
                    .willReturn(productPage);

            // when
            StoreProductPageResponseDto response = storeService.getStoreProductPage(storeId, search, pageable, userId, userRole);

            // then
            assertThat(response).isNotNull();
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(productRepository).should().searchStoreProduct(
                    eq(storeId),
                    eq("치킨"),
                    anyBoolean(),
                    any(Pageable.class)
            );
        }

        @Test
        @DisplayName("실패 - 가게를 찾을 수 없으면 예외 반환")
        void getStoreProductPage_notFound() {
            // given
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.empty());

            // when
            Throwable t = catchThrowable(() ->
                    storeService.getStoreProductPage(storeId, search, pageable, userId, userRole)
            );

            // then
            assertThat(t)
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
            then(productRepository).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("가게 수정")
    class UpdateStoreTest {

        private final UUID storeId = UUID.randomUUID();
        private final Long userId = 1L;
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 가게관리 권한이 있을경우 가게 수정")
        void updateStore_success() {
            // given
            StoreUpdateRequestDto request = storeUpdateRequestDto();
            Store store = storeEntity(storeId, userEntity(userId, userRole), false);
            Category category = mock(Category.class);
            AvailableAddress address = mock(AvailableAddress.class);
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));
            given(categoryRepository.findByIdAndDeletedAtIsNull(any(UUID.class)))
                    .willReturn(Optional.of(category));
            given(availableAddressRepository.findByRegion1depthNameAndRegion2depthName(anyString(), anyString()))
                    .willReturn(Optional.of(address));

            // when
            StoreUpdateResponseDto response = storeService.updateStore(request, storeId, userId, userRole);

            // then
            assertThat(response).isNotNull();
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(categoryRepository).should().findByIdAndDeletedAtIsNull(any(UUID.class));
            then(availableAddressRepository).should().findByRegion1depthNameAndRegion2depthName(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("가게 삭제")
    class DeleteStoreTest {

        private final UUID storeId = UUID.randomUUID();
        private final Long userId = 1L;
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 가게관리 권한이 있을경우 가게 삭제")
        void deleteStore_success() {
            // given
            User owner = userEntity(userId, userRole);
            Store store = storeEntity(storeId, owner, false);
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId))
                    .willReturn(Optional.of(store));

            // when
            storeService.deleteStore(storeId, userId, userRole);

            // then
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
        }
    }

    @Nested
    @DisplayName("가게 상태 일괄 업데이트")
    class StatusUpdateTest {

        private final Long userId = 1L;
        private final UserRoleEnum userRole = UserRoleEnum.OWNER;

        @Test
        @DisplayName("성공 - 상태 수정에 성공하면 성공리스트, 실패하면 실패리스트로 반환")
        void statusUpdate_success() {
            // given
            UUID successId = UUID.randomUUID();
            UUID unauthorizedId = UUID.randomUUID();
            UUID missingId = UUID.randomUUID();
            StoreStatusUpdateRequestDto request = new StoreStatusUpdateRequestDto(
                    List.of(successId, unauthorizedId, missingId),
                    StoreStatus.OPEN
            );

            Store successStore = storeEntity(successId, userEntity(userId, userRole), false);
            Store unauthorizedStore = storeEntity(unauthorizedId, userEntity(999L, userRole), false);
            given(storeRepository.findByIdInAndDeletedAtIsNull(anyList()))
                    .willReturn(List.of(successStore, unauthorizedStore));

            // when
            StoreStatusUpdateResponseDto response = storeService.statusUpdate(request, userId, userRole);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getSuccessIds().contains(successId)).isTrue();
            assertThat(response.getFailIds().contains(unauthorizedId)).isTrue();
            assertThat(response.getFailIds().contains(missingId)).isTrue();
            then(storeRepository).should().findByIdInAndDeletedAtIsNull(anyList());
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

    private StoreUpdateRequestDto storeUpdateRequestDto() {
        return new StoreUpdateRequestDto(
                UUID.randomUUID(),
                "수정된 가게이름", "수정된 가게 설명",
                "서울시 강남구 역삼동 123-45", "서울시", "강남구", "역삼동",
                "123-45번지", 127.0, 37.0,
                LocalTime.of(9, 0), LocalTime.of(22, 0)
        );
    }
}