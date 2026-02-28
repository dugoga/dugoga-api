package com.project.dugoga.domain.category.domain.repository;

import com.project.dugoga.domain.category.domain.model.entity.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface categoryRepository extends JpaRepository<Category, UUID> {
}
