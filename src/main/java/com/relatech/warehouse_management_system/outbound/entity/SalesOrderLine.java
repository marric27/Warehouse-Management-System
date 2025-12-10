package com.relatech.warehouse_management_system.outbound.entity;

import com.relatech.warehouse_management_system.common.util.OrderState;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_order_line")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SalesOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState status;
}
