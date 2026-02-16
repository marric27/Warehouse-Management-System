package com.relatech.warehouse_management_system.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMirrorRepository extends JpaRepository<ProductMirror, String> {

    Optional<ProductMirror> findByCode(String code);
    boolean existsByCode(String productCode);
    
}
