package com.project.dugoga.domain.category.application.service;


import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryCreateResponseDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryUpdateResponseDto;
import java.util.UUID;


public interface CategoryService {
    CategoryCreateResponseDto createCategory(CategoryCreateRequestDto dto);

    CategoryUpdateResponseDto updateCategory(CategoryUpdateRequestDto dto, UUID categoryId);
}
