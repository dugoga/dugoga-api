package com.project.dugoga.domain.category.domain.repository;

import static com.project.dugoga.global.config.generator.CategoryFixtureGenerator.generateCategoryFixture;
import static org.assertj.core.api.Assertions.assertThat;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.infrastructure.repository.CategoryRepositoryImpl;
import com.project.dugoga.global.config.DataJpaTestBase;
import com.project.dugoga.global.config.TestJpaAuditingConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("Repository: Category 레포지토리 테스트")
@Import({CategoryRepositoryImpl.class, TestJpaAuditingConfig.class})
class CategoryRepositoryTest extends DataJpaTestBase {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("기능_테스트_카테고리를_등록한다")
    void 카테고리를_등록한다() {

        // given
        Category category = generateCategoryFixture();

        // when
        Category savedCategory = categoryRepository.save(category);

        // then
        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isNotNull();
        assertThat(savedCategory.getCode()).isNotNull();

        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getUpdatedAt()).isNotNull();
        assertThat(savedCategory.getCreatedBy()).isEqualTo(1L);
        assertThat(savedCategory.getUpdatedBy()).isEqualTo(1L);
        assertThat(savedCategory.getDeletedAt()).isNull();
        assertThat(savedCategory.getDeletedBy()).isNull();
    }

    @Test
    @DisplayName("기능_테스트_해당_코드_존재여부를_확인한다")
    void 테스트_해당_코드_존재여부를_확인한다() {

        // given
        Category category = generateCategoryFixture();

        // when
        boolean result = categoryRepository.existsByCode(category.getCode());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("기능_테스트_해당_이름_존재여부를_확인한다")
    void 테스트_해당_이름_존재여부를_확인한다() {

        // given
        Category category = generateCategoryFixture();

        // when
        boolean result = categoryRepository.existsByCode(category.getName());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("기능_테스트_삭제되지_않은_카테고리의_이름_존재하지_않으면_false를_반환한다")
    void 삭제되지_않은_카테고리의_이름_존재하지_않으면_false를_반환한다() {

        // given
        Category category = generateCategoryFixture();

        // when
        boolean result = categoryRepository.existsByNameAndDeletedAtIsNull(category.getName());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("기능_테스트_삭제되지_않은_카테고리의_이름_존재하면_true를_반환한다")
    void 삭제되지_않은_카테고리의_이름_존재하면_true를_반환한다() {

        // given
        Category category = generateCategoryFixture();
        categoryRepository.save(category);

        // when
        boolean result = categoryRepository.existsByNameAndDeletedAtIsNull(category.getName());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("기능_테스트_id로_삭제되지_않은_카테고리를_조회한다")
    void id로_삭제되지_않은_카테고리를_조회한다() {

        // given
        Category category = generateCategoryFixture();
        categoryRepository.save(category);

        // when
        Optional<Category> result = categoryRepository.findByIdAndDeletedAtIsNull(category.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(category.getId());
        assertThat(result.get().getCode()).isEqualTo(category.getCode());
        assertThat(result.get().getName()).isEqualTo(category.getName());
    }
    @Test
    @DisplayName("기능_테스트_삭제되지_않은_모든_카테고리를_조회한다")
    void 삭제되지_않은_모든_카테고리를_조회한다() {

        // given
        Category category = generateCategoryFixture();
        categoryRepository.save(category);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Category> result = categoryRepository.findAllByDeletedAtIsNull(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(category.getId());
        assertThat(result.getContent().get(0).getCode()).isEqualTo(category.getCode());
        assertThat(result.getContent().get(0).getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("기능_테스트_이름에_키워드가_포함되고_삭제되지_않은_카테고리를_조회한다")
    void 이름에_키워드가_포함되고_삭제되지_않은_카테고리를_조회한다() {

        // given
        Category category = generateCategoryFixture();
        categoryRepository.save(category);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Category> result = categoryRepository.findAllByNameContainingAndDeletedAtIsNull("피자",pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(category.getName());

    }

}