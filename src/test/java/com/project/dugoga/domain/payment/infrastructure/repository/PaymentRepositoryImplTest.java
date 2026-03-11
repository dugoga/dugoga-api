package com.project.dugoga.domain.payment.infrastructure.repository;

import com.project.dugoga.config.DataJpaTestBase;
import com.project.dugoga.config.generator.*;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.order.domain.model.entity.Order;
import com.project.dugoga.domain.payment.domain.model.entity.Payment;
import com.project.dugoga.domain.payment.domain.model.enums.PaymentStatus;
import com.project.dugoga.domain.payment.domain.repository.PaymentRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import({PaymentRepositoryImpl.class, PaymentCustomRepository.class, QueryDslConfig.class})
class PaymentRepositoryImplTest extends DataJpaTestBase {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EntityManager em;

    @Nested
    @DisplayName("결제 저장")
    class SavePaymentTest {

        @Test
        @DisplayName("성공 - 결제 저장")
        void save_success() {
            // given
            User user = persistUser();
            Category catChk = CategoryFixtureGenerator.generateCategoryFixture("CHK", "치킨");
            AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
            em.persist(catChk);
            em.persist(address);

            Store store = persistStore(user, catChk, address, "교촌치킨");

            Order order = persistOrder(user, store);

            Payment payment = PaymentFixtureGenerator.generatePaymentFixture(
                    user,
                    order,
                    UUID.randomUUID().toString(),
                    PaymentStatus.PAID
            );

            // when
            Payment savedPayment = paymentRepository.save(payment);
            em.flush();
            em.clear();

            // then
            assertThat(savedPayment.getId()).isNotNull();

            Payment found = em.find(Payment.class, savedPayment.getId());
            assertThat(found).isNotNull();
            assertThat(found.getUser().getId()).isEqualTo(user.getId());
            assertThat(found.getOrder().getId()).isEqualTo(order.getId());
            assertThat(found.getStatus()).isEqualTo(PaymentStatus.PAID);
        }
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
