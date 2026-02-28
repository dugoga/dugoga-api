package com.project.dugoga.domain.category.application.service;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;

import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import com.project.dugoga.global.exception.BusinessException;
import com.project.dugoga.global.exception.ErrorCode;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public CategoryCreateResponseDto createCategory(CategoryCreateRequestDto dto) {

        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_CODE);
        }
        if (categoryRepository.existsByName(dto.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }
        Category category = Category.create(dto.getName(), dto.getCode());
        categoryRepository.save(category);

        return new CategoryCreateResponseDto(category.getId(),category.getCreatedAt());
    }

    @Transactional
    @Override
    public CategoryUpdateResponseDto updateCategory(CategoryUpdateRequestDto dto, UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String newName = dto.getName().trim();
        String newCode = dto.getCode().trim().toUpperCase();

        // 기존 이름과 동일하지 않다면, 동일한 이름이 있을 경우 -> 예외처리
        // 기존 이름과 동일하다면 -> skip
        if (!category.getName().equals(newName) && categoryRepository.existsByName(newName)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        if (!category.getCode().equals(newCode) && categoryRepository.existsByCode(newCode)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY_CODE);
        }

        // 변동되지 않았다면 그대로 응답
        if (category.getName().equals(newName) && category.getCode().equals(newCode)) {
            return new CategoryUpdateResponseDto(category.getId(), category.getUpdatedAt());
        }

        category.update(newName, newCode);

        return new CategoryUpdateResponseDto(category.getId(), category.getUpdatedAt());
    }
}
