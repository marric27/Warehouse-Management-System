package com.relatech.warehouse_management_system.warehouse.entity;

import com.relatech.warehouse_management_system.common.util.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findByCode(String code);

    List<Slot> findByAllowedCategory(Category category);

    @Query("SELECT DISTINCT s FROM Slot s JOIN s.stockUnits su WHERE su.productCode = :productCode")
    List<Slot> findDistinctByStockUnitsProductCode(@Param("productCode") String productCode);

    //List<Slot> findSlotContainingProduct(@Param("productCode") String productCode);
}
