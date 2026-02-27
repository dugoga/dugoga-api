package com.project.dugoga.domain.category.repository;

import com.project.dugoga.domain.category.entity.Category;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface categoryRepository extends JpaRepository<Category, UUID> {
}
