package com.project.dugoga.domain.bookmark.domain.repository;


import static com.project.dugoga.config.generator.AvailableAddressFixtureGenerator.generateAvailableAddressFixture;
import static com.project.dugoga.config.generator.CategoryFixtureGenerator.generateCategoryFixture;
import static com.project.dugoga.config.generator.BookmarkFixtureGenerator.generateBookmarkFixture;
import static com.project.dugoga.config.generator.StoreFixtureGenerator.generateStoreFixture;
import static com.project.dugoga.config.generator.UserFixtureGenerator.generateUserFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.project.dugoga.domain.availableaddress.domain.model.entity.AvailableAddress;
import com.project.dugoga.domain.availableaddress.infrastructure.repository.AvailableAddressJpaRepository;
import com.project.dugoga.domain.bookmark.domain.model.entity.Bookmark;
import com.project.dugoga.domain.bookmark.infrastructure.repository.BookmarkCustomRepository;
import com.project.dugoga.domain.bookmark.infrastructure.repository.BookmarkJpaRepository;
import com.project.dugoga.domain.bookmark.infrastructure.repository.BookmarkRepositoryImpl;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.infrastructure.repository.CategoryJpaRepository;
import com.project.dugoga.domain.store.domain.model.entity.Store;
import com.project.dugoga.domain.store.infrastructure.repository.StoreJpaRepository;
import com.project.dugoga.domain.user.domain.model.entity.User;
import com.project.dugoga.domain.user.infrastructure.repository.UserJpaRepository;
import com.project.dugoga.config.DataJpaTestBase;
import com.project.dugoga.global.config.QueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@DisplayName("Repository: Bookmark 레포지토리 테스트")
@Import({
                BookmarkRepositoryImpl.class,
                BookmarkCustomRepository.class,
                QueryDslConfig.class
        })
class BookmarkRepositoryTest extends DataJpaTestBase {

    @Autowired
    private BookmarkCustomRepository bookmarkCustomRepository;

    @Autowired
    private BookmarkJpaRepository bookmarkJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private StoreJpaRepository storeJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private AvailableAddressJpaRepository availableAddressJpaRepository;



    @Test
    @DisplayName("기능_테스트_회원과_음식점_중복여부를_확인한다")
    void 회원과_음식점_중복여부를_확인한다() {

        User user = generateUserFixture();
        Category category = generateCategoryFixture();
        AvailableAddress availableAddress = generateAvailableAddressFixture();
        Store store = generateStoreFixture(user, category, availableAddress);
        Bookmark bookmark = generateBookmarkFixture(user, store);

        // When
        boolean result = bookmarkJpaRepository.existsByUser_IdAndStore_Id(user.getId(), store.getId());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("기능_테스트_즐겨찾기_등록하면_저장된다")
    void 즐겨찾기_등록하면_저장된다() {

        User user = generateUserFixture();
        Category category = generateCategoryFixture();
        AvailableAddress availableAddress = generateAvailableAddressFixture();

        User savedUser = userJpaRepository.save(user);
        Category savedCategory = categoryJpaRepository.save(category);
        AvailableAddress savedAvailableAddress = availableAddressJpaRepository.save(availableAddress);

        Store store = generateStoreFixture(savedUser, savedCategory, savedAvailableAddress);
        Store savedStore = storeJpaRepository.save(store);

        Bookmark bookmark = Bookmark.of(savedUser, savedStore);
        bookmarkJpaRepository.save(bookmark);

        // When
        Bookmark result = bookmarkJpaRepository.save(bookmark);

        // Then
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getCreatedBy()).isNotNull();

    }

    @Test
    @DisplayName("기능_테스트_키워드_없이_즐겨찾기_관리자_조회")
    void 키워드_없이_즐겨찾기_관리자_조회() {


        User user = generateUserFixture();
        Category category = generateCategoryFixture();
        AvailableAddress availableAddress = generateAvailableAddressFixture();

        User savedUser = userJpaRepository.save(user);
        Category savedCategory = categoryJpaRepository.save(category);
        AvailableAddress savedAvailableAddress = availableAddressJpaRepository.save(availableAddress);

        Store store = generateStoreFixture(savedUser, savedCategory, savedAvailableAddress);
        Store savedStore = storeJpaRepository.save(store);

        bookmarkJpaRepository.save(Bookmark.of(savedUser, savedStore));

        Pageable pageable = PageRequest.of(0, 10);
        boolean isAdmin = true;

        // when
        Page<Bookmark> result =
                bookmarkCustomRepository.search(null, user.getId(), isAdmin, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("기능_테스트_숨김처리된_즐겨찾기는_일반사용자가_조회할_수_없다")
    void 숨김처리된_즐겨찾기는_일반사용자가_조회할_수_없다() {


        User user = generateUserFixture();
        Category category = generateCategoryFixture();
        AvailableAddress availableAddress = generateAvailableAddressFixture();

        User savedUser = userJpaRepository.save(user);
        Category savedCategory = categoryJpaRepository.save(category);
        AvailableAddress savedAvailableAddress = availableAddressJpaRepository.save(availableAddress);

        Store store = generateStoreFixture(savedUser, savedCategory, savedAvailableAddress);
        Store savedStore = storeJpaRepository.save(store);

        Bookmark bookmark = Bookmark.of(savedUser, savedStore);
        bookmark.updateVisibility(true);
        bookmarkJpaRepository.save(bookmark);

        Pageable pageable = PageRequest.of(0, 10);
        boolean isAdmin = false;

        // when
        Page<Bookmark> result =
                bookmarkCustomRepository.search(null, user.getId(), isAdmin, pageable);

        // then
        assertThat(result.getContent()).hasSize(0);
    }

    @Test
    @DisplayName("기능_테스트_키워드로_가게명을_검색할_수_있다")
    void 키워드로_가게명을_검색할_수_있다() {
        User user = generateUserFixture();
        Category category = generateCategoryFixture();
        AvailableAddress availableAddress = generateAvailableAddressFixture();

        User savedUser = userJpaRepository.save(user);
        Category savedCategory = categoryJpaRepository.save(category);
        AvailableAddress savedAvailableAddress = availableAddressJpaRepository.save(availableAddress);

        Store store = generateStoreFixture(savedUser, savedCategory, savedAvailableAddress);
        Store savedStore = storeJpaRepository.save(store);

        Bookmark bookmark = Bookmark.of(savedUser, savedStore);
        bookmark.updateVisibility(true);
        bookmarkJpaRepository.save(bookmark);

        Pageable pageable = PageRequest.of(0, 10);
        boolean isAdmin = true;

        // when
        Page<Bookmark> result =
                bookmarkCustomRepository.search("피자", user.getId(), isAdmin, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }
}