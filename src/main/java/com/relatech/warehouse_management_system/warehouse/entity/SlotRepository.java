package com.relatech.warehouse_management_system.warehouse.entity;

import com.relatech.warehouse_management_system.common.util.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findByCode(String code);

    List<Slot> findByAllowedCategory(Category category);
}
