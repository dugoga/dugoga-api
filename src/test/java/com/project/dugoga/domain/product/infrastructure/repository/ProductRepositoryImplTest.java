package com.project.dugoga.domain.product.infrastructure.repository;

import com.project.dugoga.config.config.DataJpaTestBase;
import com.project.dugoga.config.config.generator.*;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.product.domain.model.entity.Product;
import com.project.dugoga.domain.product.domain.repository.ProductRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.domain.model.enums.UserRoleEnum;
import com.project.dugoga.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import({ProductRepositoryImpl.class, QueryDslConfig.class, ProductCustomRepository.class})
class ProductRepositoryImplTest extends DataJpaTestBase {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    /*
        @Query("select p from Product  p
        join fetch  p.store join fetch p.store.user
        where p.id = :productId and p.deletedAt is null")
     */
    @Test
    @DisplayName("성공 - 상품 조회 시 가게와 가게주인을 한번에 조회(JPQL)")
    void findByIdWithStore_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        Category category = CategoryFixtureGenerator.generateCategoryFixture();
        Store store = StoreFixtureGenerator.generateStoreFixture(owner, category, address);
        em.persist(owner);
        em.persist(address);
        em.persist(category);
        em.persist(store);

        Product product = ProductFixtureGenerator.generateProductFixture(store);
        UUID savedProductId = productRepository.save(product).getId();

        em.flush();
        em.clear();

        // when
        Optional<Product> result = productRepository.findByIdWithStoreAndDeletedAtIsNull(savedProductId);

        // then
        assertThat(result).isPresent();
        Product foundProduct = result.get();
        assertThat(foundProduct.getStore()).isNotNull();
        assertThat(foundProduct.getStore().getUser()).isNotNull();
        assertThat(foundProduct.getStore().getUser().getId()).isEqualTo(owner.getId());
    }

    /*
        @Query("SELECT p FROM Product p
        JOIN FETCH p.store
        WHERE p.id IN :productIds and p.deletedAt is null")
     */
    @Test
    @DisplayName("성공 - 상품id 리스트로 조회 시 가게들을 한번에 조회(JPQL)")
    void findAllByIdInWithStore_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        Category category = CategoryFixtureGenerator.generateCategoryFixture();
        Store store = StoreFixtureGenerator.generateStoreFixture(owner, category, address);
        em.persist(owner);
        em.persist(address);
        em.persist(category);
        em.persist(store);

        Product product1 = ProductFixtureGenerator.generateProductFixture(store);
        Product product2 = ProductFixtureGenerator.generateProductFixture(store);
        Product product3 = ProductFixtureGenerator.generateProductFixture(store);
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        em.flush();
        em.clear();

        List<UUID> productIds = List.of(product1.getId(), product2.getId(), product3.getId());

        // when
        List<Product> results = productRepository.findAllByIdInWithStoreAndDeletedAtIsNull(productIds);

        // then
        assertThat(results.size()).isEqualTo(3);
        for (Product p : results) {
            assertThat(p.getStore()).isNotNull();
            assertThat(Hibernate.isInitialized(p.getStore())).isTrue();
            assertThat(p.getStore().getName()).isEqualTo(store.getName());
        }
    }

    @Test
    @DisplayName("성공 - 키워드와 조회 권한이있을때, 숨김 상품까지 조회(QueryDSL)")
    void searchStoreProduct_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        owner.updateUserRole(UserRoleEnum.OWNER);
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        Category category = CategoryFixtureGenerator.generateCategoryFixture();
        Store store = StoreFixtureGenerator.generateStoreFixture(owner, category, address);
        em.persist(owner);
        em.persist(address);
        em.persist(category);
        em.persist(store);

        Product product1 = ProductFixtureGenerator.generateProductFixture(
                store, "페퍼로니 피자", "맛있다.", 10000, "image"
        );
        Product product2 = ProductFixtureGenerator.generateProductFixture(
                store, "치즈 피자", "맛있다.", 10000, "image"
        );
        product1.updateIsHidden(true);
        product2.updateIsHidden(true);
        productRepository.save(product1);
        productRepository.save(product2);

        em.flush();
        em.clear();

        String keyword = "치즈";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Product> result = productRepository.searchStoreProduct(store.getId(), keyword, true, pageable);

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).contains(keyword);
    }

    @Test
    @DisplayName("성공 - 권한이 없는 사용자는 숨김처리 된 상품 조회불가(QueryDSL)")
    void searchProduct_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        Category category = CategoryFixtureGenerator.generateCategoryFixture();
        em.persist(owner);
        em.persist(address);
        em.persist(category);
        Store store1 = StoreFixtureGenerator.generateStoreFixture(owner, category, address);
        Store store2 = StoreFixtureGenerator.generateStoreFixture(owner, category, address);
        em.persist(store1);
        em.persist(store2);

        Product product1 = ProductFixtureGenerator.generateProductFixture(
                store1, "페퍼로니 피자", "맛있다.", 10000, "image"
        );
        Product product2 = ProductFixtureGenerator.generateProductFixture(
                store2, "치즈 피자", "맛있다.", 10000, "image"
        );
        product2.updateIsHidden(true);
        productRepository.save(product1);
        productRepository.save(product2);

        em.flush();
        em.clear();

        String keyword = "피자";
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Product> normalResult = productRepository.searchProduct(keyword, false, pageable);
        Page<Product> adminResult = productRepository.searchProduct(keyword, true, pageable);

        // then
        assertThat(normalResult.getContent().size()).isEqualTo(1);
        assertThat(normalResult.getContent().get(0).getName()).isEqualTo("페퍼로니 피자");
        assertThat(adminResult.getContent().size()).isEqualTo(2);
    }
}