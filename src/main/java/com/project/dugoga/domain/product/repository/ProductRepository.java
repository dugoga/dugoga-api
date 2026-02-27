package com.project.dugoga.domain.product.repository;

import com.project.dugoga.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
