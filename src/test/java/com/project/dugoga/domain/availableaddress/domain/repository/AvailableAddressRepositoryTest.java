package com.project.dugoga.domain.availableaddress.domain.repository;


import static com.project.dugoga.global.config.generator.AvailableAddressFixtureGenerator.generateAvailableAddressFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.domain.repository.AvailableAddressRepositoryTest.QuerydslConfig;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.AvailableAddressCustomRepository;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.AvailableAddressJpaRepository;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.AvailableAddressRepositoryImpl;
import com.project.dugoga.global.config.DataJpaTestBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Import({
        QuerydslConfig.class,
        AvailableAddressCustomRepository.class,
        AvailableAddressRepositoryImpl.class
        })
@DisplayName("Repository: AvailableAddress 레포지토리 테스트")
class AvailableAddressRepositoryTest extends DataJpaTestBase {


    @Autowired
    private AvailableAddressJpaRepository availableAddressJpaRepository;

    @Autowired
    private AvailableAddressRepository availableAddressRepository;

    @TestConfiguration
    static class QuerydslConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }


    @Test
    @DisplayName("주소명으로 존재하는지 조회한다.")
    void exists_false() {


        // when
        boolean exists = availableAddressJpaRepository
                .existsByRegion1depthNameAndRegion2depthName("서울시", "구로구");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("주소명으로 주소를 조회한다")
    void findByRegion_success() {

        AvailableAddress availableAddress = generateAvailableAddressFixture();
        availableAddressJpaRepository.save(availableAddress);

        // when
        Optional<AvailableAddress> result =
                availableAddressJpaRepository
                        .findByRegion1depthNameAndRegion2depthName("서울특별시", "강남구");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getRegion1depthName()).isEqualTo("서울특별시");
        assertThat(result.get().getRegion2depthName()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("삭제되지 않은 지역 조회")
    void findById_success() {
        // given
        AvailableAddress availableAddress = generateAvailableAddressFixture();
        AvailableAddress saved = availableAddressJpaRepository.save(availableAddress);

        // when
        Optional<AvailableAddress> result =
                availableAddressJpaRepository.findByIdAndDeletedAtIsNull(saved.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }


    @Test
    @DisplayName("실패 - 존재하지 않는 지역 조회")
    void findById_fail() {
        // given
        UUID randomId = UUID.randomUUID();

        // when
        Optional<AvailableAddress> result =
                availableAddressJpaRepository.findByIdAndDeletedAtIsNull(randomId);

        // then
        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("성공 - region1depthName 검색한다.")
    void search_region1() {

        // given
        availableAddressJpaRepository.save(
                AvailableAddress.of("서울시", "은평구"));
        availableAddressJpaRepository.save(
                AvailableAddress.of("부산시", "마포구"));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AvailableAddress> result =
                availableAddressRepository.search("서울", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRegion1depthName())
                .isEqualTo("서울시");
    }


    @Test
    @DisplayName("서비스 지역을 저장한다.")
    void save_success() {
        // given
        AvailableAddress address = AvailableAddress.of("서울시", "0000구");

        // when
        AvailableAddress saved = availableAddressJpaRepository.save(address);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRegion1depthName()).isEqualTo("서울시");
        assertThat(saved.getRegion2depthName()).isEqualTo("0000구");
    }


    @Test
    @DisplayName("성공 - 키워드 없이 전체 조회")
    void search_all() {

        // given
        availableAddressJpaRepository.save(
                AvailableAddress.of("서울시", "강남구"));
        availableAddressJpaRepository.save(
                AvailableAddress.of("서울시", "노원구"));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AvailableAddress> result =
                availableAddressRepository.search(null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("성공 - 삭제된 데이터는 검색되지 않음")
    void search_deleted_excluded() {

        // given
        AvailableAddress deletedAddress =
                availableAddressJpaRepository.save(
                        AvailableAddress.of("서울시", "은평구"));

        AvailableAddress activeAddress =
                availableAddressJpaRepository.save(
                        AvailableAddress.of("서울시", "마포구"));

        deletedAddress.delete(1L);
        availableAddressJpaRepository.save(deletedAddress);
        availableAddressJpaRepository.save(activeAddress);


        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AvailableAddress> result =
                availableAddressRepository.search("서울", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRegion2depthName())
                .isEqualTo("마포구");
    }
}

