package com.project.dugoga.domain.product.domain.repository;

import com.project.dugoga.domain.product.domain.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
