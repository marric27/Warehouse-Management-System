package com.relatech.warehouse_management_system.product;

import java.util.Optional;

import com.relatech.warehouse_management_system.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMirrorRepository extends JpaRepository<Product, String> {

    Optional<Product> findByCode(String code);
    boolean existsByCode(String productCode);
    
}
