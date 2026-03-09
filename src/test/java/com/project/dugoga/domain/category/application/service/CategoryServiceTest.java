package com.project.dugoga.domain.category.application.service;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Nested
    @DisplayName("카테고리 생성")
    class CreateCategoryTest {

        @Test
        @DisplayName("성공 - 카테고리 생성")
        void createCategory_success() {

            // given
            CategoryCreateRequestDto request = new CategoryCreateRequestDto("KOR", "한식");
            Category category = Category.create("KOR", "한식");

            given(categoryRepository.existsByName("한식")).willReturn(false);
            given(categoryRepository.existsByCode("KOR")).willReturn(false);
            given(categoryRepository.save(any(Category.class)))
                    .willReturn(category);

            // when
            CategoryCreateResponseDto response = categoryService.createCategory(request);

            // then
            assertThat(response).isNotNull();

            // 호출 검증
            then(categoryRepository).should().existsByName("한식");
            then(categoryRepository).should().existsByCode("KOR");
            then(categoryRepository).should().save(any(Category.class));

        }

        @Test
        @DisplayName("실패 - 카테고리 이름이 중복되면 예외 발생")
        void createCategory_duplicateName() {
            // given
            CategoryCreateRequestDto request = new CategoryCreateRequestDto("KOR", "한식");

            given(categoryRepository.existsByName("한식")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.DUPLICATE_CATEGORY_NAME.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 카테고리 코드가 중복되면 예외 발생")
        void createCategory_duplicateCode() {
            // given
            CategoryCreateRequestDto request =
                    new CategoryCreateRequestDto("KOR", "한식");

            given(categoryRepository.existsByName("한식")).willReturn(false);
            given(categoryRepository.existsByCode("KOR")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.DUPLICATE_CATEGORY_CODE.getDefaultMessage());
        }
    }

    @Nested
    @DisplayName("카테고리 수정")
    class updateCategoryTest {

        @Test
        @DisplayName("성공 - 카테고리 수정")
        void updateCategory_success() {



            UUID categoryId = UUID.randomUUID();
            Category category = Category.create("KOR", "한식");
            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(" JPN", "일식  ");
            String newName = request.getName().trim();
            String newCode = request.getCode().trim().toUpperCase();

            // given
            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId)).willReturn(Optional.of(category));
            given(categoryRepository.existsByName(newName)).willReturn(false);
            given(categoryRepository.existsByCode(newCode)).willReturn(false);

            // when
            CategoryUpdateResponseDto response = categoryService.updateCategory(request, categoryId);

            // then
            assertThat(response).isNotNull();
            assertThat(category.getName()).isEqualTo(newName);
            assertThat(category.getCode()).isEqualTo(newCode);

            then(categoryRepository).should().findByIdAndCreatedAtIsNull(categoryId);
            then(categoryRepository).should().existsByName(newName);
            then(categoryRepository).should().existsByCode(newCode);

        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리면 예외 발생")
        void updateCategory_notFound() {

            UUID categoryId = UUID.randomUUID();
            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto("JPA", "일식");
            // given

            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(request, categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 삭제된 카테고리면 예외 발생")
        void updateCategory_deletedCategory() {

            UUID categoryId = UUID.randomUUID();
            Category category = Category.create("KOR", "한식");
            category.delete(1L); // 삭제 상태로 만들기

            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto("JPA", "일식");

            // given
            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId))
                    .willReturn(Optional.of(category));

            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(request, categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.CATEGORY_ALREADY_DELETED.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 카테고리 이름이 중복되면 예외 발생")
        void updateCategory_duplicateName() {

            UUID categoryId = UUID.randomUUID();
            Category category = Category.create("KOR", "한식");

            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(" JPA", "일식 ");

            String newName = request.getName().trim();

            // given
            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId)).willReturn(Optional.of(category));
            given(categoryRepository.existsByName(newName)).willReturn(true);


            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(request, categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.DUPLICATE_CATEGORY_NAME.getDefaultMessage());
        }

        @Test
        @DisplayName("실패 - 카테고리 코드가 중복되면 예외 발생")
        void updateCategory_duplicateCode() {

            UUID categoryId = UUID.randomUUID();
            Category category = Category.create("KOR", "한식");

            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto(" JPA", "일식 ");

            String newCode = request.getCode().trim().toUpperCase();

            // given
            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId)).willReturn(Optional.of(category));
            given(categoryRepository.existsByName("일식")).willReturn(false); // 이름 중복 x
            given(categoryRepository.existsByCode(newCode)).willReturn(true); // 코드 중복 o


            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(request, categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.DUPLICATE_CATEGORY_CODE.getDefaultMessage());
        }
        @Test
        @DisplayName("실패 - 이름과 코드가 모두 기존과 같으면 예외 발생")
        void updateCategory_noChanges() {

            UUID categoryId = UUID.randomUUID();
            Category category = Category.create("KOR", "한식");
            CategoryUpdateRequestDto request = new CategoryUpdateRequestDto("KOR", "한식");

            // given
            given(categoryRepository.findByIdAndCreatedAtIsNull(categoryId)).willReturn(Optional.of(category));

            // when & then
            assertThatThrownBy(() -> categoryService.updateCategory(request, categoryId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage());
        }

    }
    @Nested
    @DisplayName("카테고리 식제")
    class DeleteCategoryTest {
        @Test
        @DisplayName("성공 - 카테고리 삭제")
        void deleteCategory_success() {

            UUID categoryId = UUID.randomUUID();
            Long userId = 1L;
            Category category = Category.create("KOR", "한식");


            // given
            given(categoryRepository.findByIdAndDeletedAtIsNull(categoryId)).willReturn(Optional.of(category));

            // when
            categoryService.deleteCategory(categoryId, userId);

            // then
            then(categoryRepository).should().findByIdAndDeletedAtIsNull(categoryId);

            assertThat(category.getDeletedAt()).isNotNull();
            assertThat(category.getDeletedBy()).isEqualTo(userId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리이면 예외 발생")
        void deleteCategory_notFound() {

            UUID categoryId = UUID.randomUUID();
            Long userId = 1L;

            // given
            given(categoryRepository.findByIdAndDeletedAtIsNull(categoryId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> categoryService.deleteCategory(categoryId, userId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getDefaultMessage());
        }

    }
}