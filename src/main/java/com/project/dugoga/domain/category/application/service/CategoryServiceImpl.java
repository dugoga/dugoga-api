package com.project.dugoga.domain.category.application.service;

import com.project.dugoga.domain.category.application.dto.CategoryCreateRequestDto;
import com.project.dugoga.domain.category.application.dto.CategoryResponseDto;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDto createCategory(CategoryCreateRequestDto dto) {



        Category category = Category.create(dto.getName(), dto.getCode());
        categoryRepository.save(category);

        return new CategoryResponseDto(category.getId(),category.getCreatedAt());
    }
}
