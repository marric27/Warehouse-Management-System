package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.outbound.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByCode(String code);

    Page<Order> findByCustomerCode(String customerCode, Pageable pageable);

    Page<Order> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.salesOrderLineList l WHERE l.productId = :productId")
    Page<Order> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.salesOrderLineList l " +
            "WHERE (:customerCode IS NULL OR o.customerCode = :customerCode) " +
            "AND (:productId IS NULL OR l.productId = :productId) " +
            "AND (:start IS NULL OR :end IS NULL OR o.date BETWEEN :start AND :end)")
    Page<Order> filterOrders(@Param("customerCode") String customerCode,
                             @Param("productId") Long productId,
                             @Param("start") LocalDate startDate,
                             @Param("end") LocalDate endDate,
                             Pageable pageable);
}
