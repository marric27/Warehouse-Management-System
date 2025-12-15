package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.outbound.entity.PickList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickListRepository extends JpaRepository<PickList,Long> {
}
