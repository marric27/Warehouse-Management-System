package com.relatech.warehouse_management_system.grnItem.repository;

import com.relatech.warehouse_management_system.grnItem.entity.GrnItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrnItemRepository extends JpaRepository<GrnItem, Long> {
}
