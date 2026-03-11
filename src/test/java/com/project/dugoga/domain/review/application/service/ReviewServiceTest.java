package com.project.dugoga.domain.review.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.project.dugoga.domain.aiprompt.application.service.AiPromptService;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.review.application.dto.ReviewCreateRequestDto;
import com.project.dugoga.domain.review.application.dto.ReviewCreateResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetDetailResponseDto;
import com.project.dugoga.domain.review.application.dto.ReviewGetListResponseDto;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.review.domain.repository.ReviewRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AiPromptService aipromptService;

    @InjectMocks
    private ReviewService reviewService;

    @Nested
    @DisplayName("리뷰 생성")
    class CreateReviewTest {

        @Test
        @DisplayName("성공 - 리뷰 생성")
        void createReview_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            UUID reviewId = UUID.randomUUID();

            User user = User.of("email@test.com", "passWord1@", "hong", "xiest", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);

            Order order = Order.create(user, store, "요청사항 없음", 15000, 3000);
            ReflectionTestUtils.setField(order, "id", orderId);

            int rating = 3;
            String content = "그냥 먹을만해요.";
            String imageUrl = "../../../";

            ReviewCreateRequestDto requestDto = new ReviewCreateRequestDto(storeId, orderId, rating, content, imageUrl);

            Review savedReview = Review.builder()
                    .storeId(store).userId(user).orderId(order).rating(rating).content(content)
                    .imageUrl(imageUrl).isHidden(false).build();
            ReflectionTestUtils.setField(savedReview, "id", reviewId);

            given(aipromptService.getReviewAiPromptText(content)).willReturn("성공");
            given(reviewRepository.existsByOrderId_Id(orderId)).willReturn(false);
            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.of(store));
            given(orderRepository.findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId)).willReturn(Optional.of(order));
            given(reviewRepository.save(any(Review.class))).willReturn(savedReview);

            // when
            ReviewCreateResponseDto responseDto = reviewService.createReview(requestDto, userId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(reviewId);

            then(aipromptService).should().getReviewAiPromptText(content);
            then(reviewRepository).should().existsByOrderId_Id(orderId);
            then(userRepository).should().findByIdAndDeletedAtIsNull(userId);
            then(storeRepository).should().findByIdAndDeletedAtIsNull(storeId);
            then(orderRepository).should().findByIdAndUser_IdAndDeletedAtIsNull(orderId, userId);
            then(reviewRepository).should().save(any(Review.class));
        }

        @Test
        @DisplayName("실패 - 리뷰가 이미 존재하는 경우")
        void createReview_fail_already_exist() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            String content = "그냥 먹을만해요.";

            ReviewCreateRequestDto requestDto = new ReviewCreateRequestDto(storeId, orderId, 3, content, "../../../");

            given(aipromptService.getReviewAiPromptText(content)).willReturn("성공");
            given(reviewRepository.existsByOrderId_Id(orderId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(requestDto, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.REVIEW_ALREADY_EXISTS.getDefaultMessage());

            then(aipromptService).should().getReviewAiPromptText(content);
            then(reviewRepository).should().existsByOrderId_Id(orderId);
            then(userRepository).shouldHaveNoInteractions();
            then(storeRepository).shouldHaveNoInteractions();
            then(orderRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 리뷰에 비속어가 있는 경우")
        void createReview_fail_inappropriate_review() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            String content = "돈 버렸어요, 젠장";
            String aiResponse = "실패: 욕설 감지";

            ReviewCreateRequestDto requestDto = new ReviewCreateRequestDto(storeId, orderId, 1, content, null);

            given(aipromptService.getReviewAiPromptText(content)).willReturn(aiResponse);
            given(reviewRepository.existsByOrderId_Id(orderId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(requestDto, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(aiResponse);

            then(aipromptService).should().getReviewAiPromptText(content);
            then(reviewRepository).should().existsByOrderId_Id(orderId);
            then(userRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 회원인 경우")
        void createReview_fail_user_not_found() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            String content = "맛있어요.";

            ReviewCreateRequestDto requestDto = new ReviewCreateRequestDto(storeId, orderId, 5, content, null);

            given(aipromptService.getReviewAiPromptText(content)).willReturn("성공");
            given(reviewRepository.existsByOrderId_Id(orderId)).willReturn(false);
            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(requestDto, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 상점이나 주문내역이 없는 경우")
        void createReview_fail_store_or_order_not_found() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            String content = "맛있어요.";

            User user = User.of("email@test.com", "passWord1@", "hong", "xiest", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            ReviewCreateRequestDto requestDto = new ReviewCreateRequestDto(storeId, orderId, 4, content, null);

            given(aipromptService.getReviewAiPromptText(content)).willReturn("성공");
            given(reviewRepository.existsByOrderId_Id(orderId)).willReturn(false);
            given(userRepository.findByIdAndDeletedAtIsNull(userId)).willReturn(Optional.of(user));
            given(storeRepository.findByIdAndDeletedAtIsNull(storeId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.createReview(requestDto, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("리뷰 목록 조회")
    class GetReviewListTest {

        @Test
        @DisplayName("성공 - 고객 리뷰 목록 조회")
        void getCustomerReview_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            User user = User.of("email@test.com", "pw", "name", "nick", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);

            Order order = Order.create(user, store, "요청사항 없음", 15000, 3000);
            ReflectionTestUtils.setField(order, "id", orderId);

            Review review = Review.builder().storeId(store).userId(user).orderId(order)
                    .rating(5).content("").isHidden(false).build();
            ReflectionTestUtils.setField(review, "id", UUID.randomUUID());

            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
            given(reviewRepository.findAllByUserId_IdAndDeletedAtIsNull(eq(userId), any(Pageable.class))).willReturn(reviewPage);

            // when
            ReviewGetListResponseDto responseDto = reviewService.getCustomerReview(pageable, userId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getReviewList()).hasSize(1);
            then(reviewRepository).should().findAllByUserId_IdAndDeletedAtIsNull(eq(userId), any(Pageable.class));
        }

        @Test
        @DisplayName("성공 - 상점 리뷰 목록 조회")
        void getStoreReview_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            User user = User.of("email@test.com", "pw", "name", "nick", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);

            Order order = Order.create(user, store, "요청사항 없음", 15000, 3000);
            ReflectionTestUtils.setField(order, "id", orderId);

            Review review = Review.builder().storeId(store).userId(user).orderId(order)
                    .rating(5).content("").isHidden(false).build();
            ReflectionTestUtils.setField(review, "id", UUID.randomUUID());

            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
            given(reviewRepository.findAllByStoreId_IdAndDeletedAtIsNull(eq(storeId), any(Pageable.class))).willReturn(reviewPage);

            // when
            ReviewGetListResponseDto responseDto = reviewService.getStoreReview(pageable, storeId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getReviewList()).hasSize(1);
            then(reviewRepository).should().findAllByStoreId_IdAndDeletedAtIsNull(eq(storeId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("리뷰 상세 조회 및 삭제")
    class GetDetailAndDeleteReviewTest {

        @Test
        @DisplayName("성공 - 리뷰 상세 조회")
        void getDetailReview_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            UUID reviewId = UUID.randomUUID();

            User user = User.of("email@test.com", "pw", "name", "nick", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);
            ReflectionTestUtils.setField(store, "averageRating", 5.0);
            ReflectionTestUtils.setField(store, "reviewCount", 1L);

            Order order = Order.create(user, store, "요청사항 없음", 15000, 3000);
            ReflectionTestUtils.setField(order, "id", orderId);

            Review review = Review.builder().storeId(store).userId(user).orderId(order)
                    .rating(5).content("그저 그래요").isHidden(false).build();
            ReflectionTestUtils.setField(review, "id", reviewId);

            given(reviewRepository.findByIdWithStoreAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(review));

            // when
            ReviewGetDetailResponseDto responseDto = reviewService.getDetailReview(reviewId);

            // then
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(reviewId);
            then(reviewRepository).should().findByIdWithStoreAndDeletedAtIsNull(reviewId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 리뷰 조회")
        void getDetailReview_fail_not_found() {
            // given
            UUID reviewId = UUID.randomUUID();
            given(reviewRepository.findByIdWithStoreAndDeletedAtIsNull(reviewId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> reviewService.getDetailReview(reviewId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("성공 - 리뷰 삭제")
        void deleteReview_success() {
            // given
            Long userId = 7L;
            UUID storeId = UUID.randomUUID();
            UUID orderId = UUID.randomUUID();
            UUID reviewId = UUID.randomUUID();
            Long requestUserId = 10L;

            User user = User.of("email@test.com", "pw", "name", "nick", UserRoleEnum.CUSTOMER);
            ReflectionTestUtils.setField(user, "id", userId);

            Category category = Category.of("PIZ", "피자 전문점");
            AvailableAddress availableAddress = AvailableAddress.of("서울시", "강남구");

            Store store = Store.of(user, category, availableAddress, "피자 맛집", null,
                    "서울시 강남구 강남대로", "서울시", "강남구", "테헤란로", "2층", 12.2, 12.2,
                    LocalTime.of(8, 30), LocalTime.of(22, 30));
            ReflectionTestUtils.setField(store, "id", storeId);
            ReflectionTestUtils.setField(store, "averageRating", 5.0);
            ReflectionTestUtils.setField(store, "reviewCount", 1L);

            Order order = Order.create(user, store, "요청사항 없음", 15000, 3000);
            ReflectionTestUtils.setField(order, "id", orderId);

            Review review = Review.builder().storeId(store).userId(user).orderId(order)
                    .rating(5).content("삭제될 리뷰").isHidden(false).build();
            ReflectionTestUtils.setField(review, "id", reviewId);

            given(reviewRepository.findByIdWithStoreAndDeletedAtIsNull(reviewId)).willReturn(Optional.of(review));

            // when
            reviewService.deleteReview(reviewId, requestUserId);

            // then
            assertThat(review.getDeletedBy()).isEqualTo(requestUserId);
            then(reviewRepository).should().findByIdWithStoreAndDeletedAtIsNull(reviewId);
        }
    }
}