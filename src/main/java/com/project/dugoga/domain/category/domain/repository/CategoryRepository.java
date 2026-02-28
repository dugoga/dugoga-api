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
    boolean existsByCode(
            @NotBlank(message = "카테고리 코드는 필수입니다.") @Pattern(regexp = "^[A-Z0-9_]+$", message = "코드는 영문, 숫자, _만 가능합니다.") @Size(max = 20, message = "카테고리 코드는 20자 이하입니다.") String code);

    boolean existsByName(String name);
}
