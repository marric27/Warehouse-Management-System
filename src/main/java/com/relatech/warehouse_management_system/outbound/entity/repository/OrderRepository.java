package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.outbound.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByCode(String code);
}
