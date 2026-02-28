package com.project.dugoga.domain.category.application.service;


import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryResponseDto;


public interface CategoryService {
    CategoryResponseDto createCategory(CategoryCreateRequestDto dto);
}
