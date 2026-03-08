package com.project.dugoga.domain.category.application.service;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;

import com.project.dugoga.domain.category.application.dto.CategoryPageResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryCreateResponseDto createCategory(CategoryCreateRequestDto dto) {

        String name = dto.getName().trim();
        String code = dto.getCode().trim().toUpperCase();

        if (categoryRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        if (categoryRepository.existsByCode(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_CODE);
        }

        Category category = Category.create(name, code);
        Category saved = categoryRepository.save(category);

        return CategoryCreateResponseDto.from(saved);
    }

    @Transactional
    public CategoryUpdateResponseDto updateCategory(CategoryUpdateRequestDto dto, UUID categoryId) {

        Category category = categoryRepository.findByIdAndCreatedAtIsNull(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.isDeleted()) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_DELETED);
        }

        String newName = dto.getName().trim();
        String newCode = dto.getCode().trim().toUpperCase();

        if (!category.getName().equals(newName) && categoryRepository.existsByName(newName)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        if (!category.getCode().equals(newCode) && categoryRepository.existsByCode(newCode)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_CODE);
        }

        // 변동되지 않았을 경우
        if (category.getName().equals(newName) && category.getCode().equals(newCode)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        category.update(newCode, newName);

        return new CategoryUpdateResponseDto(category.getId(), category.getUpdatedAt());
    }

    @Transactional
    public void deleteCategory(UUID categoryId, Long userId) {

        Category category = categoryRepository.findByIdAndCreatedAtIsNull(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        category.delete(userId);

    }

    public CategoryPageResponseDto getCategories(String keyword, Pageable pageable) {

        Pageable normalized = normalizePageable(pageable);

        Page<Category> page = (keyword == null || keyword.isBlank())
                ? categoryRepository.findAllByDeletedAtIsNull(normalized)
                : categoryRepository.findAllByNameContainingAndDeletedAtIsNull(keyword, normalized);

        return CategoryPageResponseDto.from(page);
    }

    private Pageable normalizePageable(Pageable pageable) {

        int page = Math.max(pageable.getPageNumber(), 0);

        int requestedSize = pageable.getPageSize();
        int size = (requestedSize == 10 || requestedSize == 30 || requestedSize == 50)
                ? requestedSize
                : 10;

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Direction.DESC, "createdAt");

        return PageRequest.of(page, size, sort);
    }


}
