package com.relatech.warehouse_management_system.stockUnit.repository;

import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockUnitRepository extends JpaRepository<StockUnit, Long> {
    Optional<StockUnit> findByUniqueCode(String uniqueCode);
}

