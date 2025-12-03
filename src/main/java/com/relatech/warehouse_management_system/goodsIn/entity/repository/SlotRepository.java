package com.relatech.warehouse_management_system.goodsIn.entity.repository;

import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.goodsIn.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findByCode(String code);

    List<Slot> findByAllowedCategory(Category category);
}
