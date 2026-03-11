package com.project.dugoga.domain.category.infrastructure.repository;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import com.project.dugoga.domain.category.domain.repository.CategoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId) {
        return jpaRepository.findByIdAndDeletedAtIsNull(categoryId);
    }

    @Override
    public boolean existsByNameAndDeletedAtIsNull(String name) {
        return jpaRepository.existsByNameAndDeletedAtIsNull(name);
    }

    @Override
    public Page<Category> findAllByDeletedAtIsNull(Pageable pageable) {
        return jpaRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<Category> findAllByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable) {
        return jpaRepository.findAllByNameContainingAndDeletedAtIsNull(keyword, pageable);
    }

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }
}
