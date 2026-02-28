package com.project.dugoga.domain.category.domain.repository;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByCode(String code);

    boolean existsByName(String name);
}
