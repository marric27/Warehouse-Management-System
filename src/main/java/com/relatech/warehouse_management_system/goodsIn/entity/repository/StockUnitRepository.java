package com.relatech.warehouse_management_system.goodsIn.entity.repository;

import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockUnitRepository extends JpaRepository<StockUnit,Long>{
        Optional<StockUnit> findByCode(String code);
    }

