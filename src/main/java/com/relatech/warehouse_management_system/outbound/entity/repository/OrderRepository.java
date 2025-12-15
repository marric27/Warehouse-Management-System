package com.relatech.warehouse_management_system.outbound.entity.repository;

import com.relatech.warehouse_management_system.common.util.OrderState;
import com.relatech.warehouse_management_system.outbound.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByCode(String code);

    List<Order> findByState(OrderState orderState);

    Page<Order> findByCustomerCodeAndState(String customerCode, OrderState state, Pageable pageable);

    Page<Order> findByDateBetweenAndState(LocalDate start, LocalDate end, OrderState state, Pageable pageable);

    @Query("""
    SELECT o FROM Order o
    JOIN o.salesOrderLineList l
    WHERE l.productCode = :productCode
      AND o.state = :state
""")
    Page<Order> findByProductCodeAndState(
            @Param("productCode") String productCode,
            @Param("state") OrderState state,
            Pageable pageable
    );


    @Query("""
        SELECT o FROM Order o
        WHERE (:state IS NULL OR o.state = :state)
          AND (:ids IS NULL OR o.id IN :ids)
    """)
    List<Order> filterByStateAndIds(
            @Param("state") OrderState state,
            @Param("ids") List<Long> ids
    );

    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN o.salesOrderLineList l
        WHERE o.state = :state
          AND (:customerCode IS NULL OR o.customerCode = :customerCode)
          AND (:productCode IS NULL OR l.productCode = :productCode)
          AND (:start IS NULL OR o.date >= :start)
          AND (:end IS NULL OR o.date <= :end)
    """)
    Page<Order> filterOrders(
            @Param("state") OrderState state,
            @Param("customerCode") String customerCode,
            @Param("productCode") String productCode,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable);
}
