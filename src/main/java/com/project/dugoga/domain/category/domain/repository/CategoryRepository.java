package com.project.dugoga.domain.category.domain.repository;


import com.project.dugoga.domain.category.domain.model.entity.Category;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCode(String code);

    boolean existsByName(String name);

//    Optional<Category> findByIdAndCreatedAtIsNull(UUID categoryId);

    Optional<Category> findByIdAndDeletedAtIsNull(UUID categoryId);

    Page<Category> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Category> findAllByNameContainingAndDeletedAtIsNull(String keyword, Pageable pageable);

}
