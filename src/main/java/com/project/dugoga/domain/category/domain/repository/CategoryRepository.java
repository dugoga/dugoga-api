package com.project.dugoga.domain.category.domain.repository;


import com.project.dugoga.domain.category.domain.model.entity.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository {

    boolean existsByCode(String code);

    boolean existsByName(String name);

    Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId);

    boolean existsByNameAndDeletedAtIsNull(String name);

    Page<Category> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Category> findAllByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);

    Category save(Category category);
}
