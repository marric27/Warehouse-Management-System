package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.outbound.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> {
}
