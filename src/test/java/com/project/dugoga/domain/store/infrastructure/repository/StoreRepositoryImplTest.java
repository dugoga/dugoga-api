package com.project.dugoga.domain.store.infrastructure.repository;

import com.project.dugoga.global.config.DataJpaTestBase;
import com.project.dugoga.global.config.generator.AvailableAddressFixtureGenerator;
import com.project.dugoga.global.config.generator.CategoryFixtureGenerator;
import com.project.dugoga.global.config.generator.StoreFixtureGenerator;
import com.project.dugoga.global.config.generator.UserFixtureGenerator;
import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.domain.repository.StoreRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import({StoreRepositoryImpl.class, QueryDslConfig.class, StoreCustomRepository.class})
class StoreRepositoryImplTest extends DataJpaTestBase {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("성공 - 키워드와 카테고리로 가게 검색(QueryDSL)")
    void searchStores_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        Category category1 = CategoryFixtureGenerator.generateCategoryFixture("CAFE", "카페");
        Category category2 = CategoryFixtureGenerator.generateCategoryFixture("YANG", "양식");
        Category category3 = CategoryFixtureGenerator.generateCategoryFixture("HAN", "한식");
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        em.persist(owner);
        em.persist(category1);
        em.persist(category2);
        em.persist(category3);
        em.persist(address);

        Store store1 = StoreFixtureGenerator.generateStoreFixture(owner, category1, address,
                "두고가 커피", "강남", "부유한 동네");
        Store store2 = StoreFixtureGenerator.generateStoreFixture(owner, category2, address,
                "두고가 양식", "강남", "부유한 동네");
        Store store3 = StoreFixtureGenerator.generateStoreFixture(owner, category3, address,
                "두고가 한식", "강남", "부유한 동네");
        storeRepository.save(store1);
        storeRepository.save(store2);
        storeRepository.save(store3);

        em.flush();
        em.clear();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Store> result = storeRepository.searchStores(
                "커피", "카페", null, true, pageable
        );

        // then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("두고가 커피");
    }

    /*
        @Query("select s from Store s
        join fetch s.user join fetch s.category
        where s.id = :storeId and s.deletedAt is null")
     */
    @Test
    @DisplayName("성공 - 가게 조회 시 사용자, 카테고리를 한 번에 조회(JPQL)")
    void findByIdWithDetails_success() {
        // given
        User owner = UserFixtureGenerator.generateUserFixture();
        Category category = CategoryFixtureGenerator.generateCategoryFixture("CAFE", "카페");
        AvailableAddress address = AvailableAddressFixtureGenerator.generateAvailableAddressFixture();
        em.persist(owner);
        em.persist(category);
        em.persist(address);

        Store store = StoreFixtureGenerator.generateStoreFixture(
                owner, category, address, "두고가 커피", "강남", "테스트 상세주소"
        );
        UUID savedStoreId = storeRepository.save(store).getId();

        em.flush();
        em.clear();

        // when
        Optional<Store> result = storeRepository.findByIdWithDetailsAndDeletedAtIsNull(savedStoreId);

        // then
        assertThat(result).isPresent();
        Store foundStore = result.get();
        assertThat(foundStore.getUser().getEmail()).isEqualTo(owner.getEmail());
        assertThat(foundStore.getCategory().getName()).isEqualTo("카페");
    }

}