package com.project.dugoga.domain.order.infrastructure.repository;

import com.project.dugoga.config.DataJpaTestBase;
import com.project.dugoga.config.generator.*;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.order.domain.repository.OrderRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Import(OrderRepositoryImpl.class)
class OrderRepositoryImplTest extends DataJpaTestBase {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("성공 - 사용자 주문 목록 조회")
    void findAllWithStoreByUser_IdAndDeletedAtIsNull_success() {
        // given
        User user = persistUser();
        Category catChk = CategoryFixtureGenerator.generateCategoryFixture("CHK", "치킨");
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        em.persist(catChk);
        em.persist(address);

        Store store = persistStore(user, catChk, address, "교촌치킨");

        persistOrder(user, store);
        persistOrder(user, store);

        Pageable pageable = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        // when
        Page<Order> result = orderRepository.findAllWithStoreByUser_IdAndDeletedAtIsNull(user.getId(), pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(order -> order.getStore().getName())
                .containsOnly("교촌치킨");
    }

    @Test
    @DisplayName("성공 - 사용자 주문 목록 가게명 검색")
    void findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull_success() {
        // given
        User user = persistUser();
        Category catChk = CategoryFixtureGenerator.generateCategoryFixture("CHK", "치킨");
        Category catPiz = CategoryFixtureGenerator.generateCategoryFixture("PIZ", "피자");
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        em.persist(catChk);
        em.persist(catPiz);
        em.persist(address);

        Store chickenStore = persistStore(user, catChk, address, "교촌치킨");
        Store pizzaStore = persistStore(user, catPiz, address, "도미노피자");

        persistOrder(user, chickenStore);
        persistOrder(user, pizzaStore);

        Pageable pageable = PageRequest.of(0, 10);

        em.flush();
        em.clear();

        // when
        Page<Order> result = orderRepository
                .findAllWithStoreByUser_IdAndStore_NameContainingIgnoreCaseAndDeletedAtIsNull(
                        user.getId(), "치킨", pageable
                );

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStore().getName()).contains("치킨");
    }

    private User persistUser() {
        User user = UserFixtureGenerator.generateUserFixture();
        em.persist(user);
        return user;
    }

    private Store persistStore(User owner, Category category, AvailableAddress address, String name) {
        Store store = StoreFixtureGenerator.generateStoreFixture(owner, category, address, name);
        em.persist(store);
        return store;
    }

    private Order persistOrder(User user, Store store) {
        Order order = OrderFixtureGenerator.generateOrderFixture(user, store);
        em.persist(order);
        return order;
    }
}
