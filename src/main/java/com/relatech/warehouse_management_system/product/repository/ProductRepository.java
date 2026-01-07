package com.relatech.warehouse_management_system.product.repository;

import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    List<Product> findByCategory(Category category);
    boolean existsByCode(String code);
}
