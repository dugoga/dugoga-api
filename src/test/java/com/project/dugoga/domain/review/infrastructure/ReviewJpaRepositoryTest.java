package com.project.dugoga.domain.review.infrastructure.repository;

import com.project.dugoga.config.DataJpaTestBase;
import com.project.dugoga.config.generator.ReviewFixtureGenerator;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.review.domain.model.entity.Review;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

import static com.project.dugoga.config.generator.AvailableAddressFixtureGenerator.generateAvailableAddressFixture;
import static com.project.dugoga.config.generator.CategoryFixtureGenerator.generateCategoryFixture;
import static com.project.dugoga.config.generator.OrderFixtureGenerator.generateOrderFixture;
import static com.project.dugoga.config.generator.ReviewFixtureGenerator.generateReviewFixture;
import static com.project.dugoga.config.generator.StoreFixtureGenerator.generateStoreFixture;
import static com.project.dugoga.config.generator.UserFixtureGenerator.generateUserFixture;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Repository: Review 레포지터리 테스트")
public class ReviewJpaRepositoryTest extends DataJpaTestBase {

    @Autowired
    private ReviewJpaRepository reviewJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User savedUser;
    private Category savedCategory;
    private AvailableAddress savedAvailableAddress;
    private Store savedStore;
    private Order savedOrder;
    private Review savedReview;

    @BeforeEach
    void setUp() {
        savedUser = entityManager.persist(generateUserFixture());
        savedCategory = entityManager.persist(generateCategoryFixture());
        savedAvailableAddress = entityManager.persist(generateAvailableAddressFixture());
        savedStore = entityManager.persist(generateStoreFixture(savedUser, savedCategory, savedAvailableAddress));
        savedOrder = entityManager.persist(generateOrderFixture(savedUser, savedStore));
        savedReview = entityManager.persist(generateReviewFixture(savedUser, savedStore, savedOrder));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Create Review (save)")
    void create_review() {
        // given
        Order order = entityManager.persist(generateOrderFixture(savedUser, savedStore));
        Review review = generateReviewFixture(savedUser, savedStore, order);

        // when
        Review result = reviewJpaRepository.save(review);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId().getId()).isEqualTo(savedUser.getId());
        assertThat(result.getStoreId().getId()).isEqualTo(savedStore.getId());
        assertThat(result.getOrderId().getId()).isEqualTo(order.getId());
        assertThat(result.getRating()).isEqualTo(ReviewFixtureGenerator.RATING);
        assertThat(result.getContent()).isEqualTo(ReviewFixtureGenerator.CONTENT);
        assertThat(result.getIsHidden()).isFalse();

        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("Check Review Exists by Order ID (existsByOrderId_Id)")
    void exists_review() {
        // given
        UUID orderId = savedOrder.getId();

        // when
        boolean exists = reviewJpaRepository.existsByOrderId_Id(orderId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Read Review (findByIdAndDeletedAtIsNull)")
    void find_review() {
        // given
        UUID reviewId = savedReview.getId();

        // when
        Review result = reviewJpaRepository.findByIdAndDeletedAtIsNull(reviewId).orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reviewId);
        assertThat(result.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("Deleted Review (findByIdAndDeletedAtIsNull)")
    void deleted_review() {
        // given
        UUID reviewId = savedReview.getId();
        Long userId = savedUser.getId();

        Review delete = reviewJpaRepository.findById(reviewId).orElseThrow();
        delete.delete(userId);

        entityManager.flush();
        entityManager.clear();

        // when
        Review result = reviewJpaRepository.findByIdAndDeletedAtIsNull(reviewId).orElse(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Read User Review List (findAllByUserId_IdAndDeletedAtIsNull)")
    void find_reviews_by_user_id() {
        // given
        Long userId = savedUser.getId();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Review> result = reviewJpaRepository.findAllByUserId_IdAndDeletedAtIsNull(userId, pageRequest);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getUserId().getId()).isEqualTo(userId);
        assertThat(result.getContent().get(0).getDeletedAt()).isNull();

        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Read Store Review List (findAllByStoreId_IdAndDeletedAtIsNull)")
    void find_reviews_by_store_id() {
        // given
        UUID storeId = savedStore.getId();
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<Review> result = reviewJpaRepository.findAllByStoreId_IdAndDeletedAtIsNull(storeId, pageRequest);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getStoreId().getId()).isEqualTo(storeId);
        assertThat(result.getContent().get(0).getDeletedAt()).isNull();
    }

}