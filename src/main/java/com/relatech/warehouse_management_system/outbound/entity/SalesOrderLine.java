package com.relatech.warehouse_management_system.outbound.entity;

import com.github.f4b6a3.ulid.UlidCreator;
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

    @Column(name = "sales_order_number", nullable = false, unique = true) //TODO intero progressivo
    private String salesOrderNumber;

    @PrePersist
    public void prePersist() {
        if (salesOrderNumber == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.salesOrderNumber = "SO-" + ulid;
        }
    }

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderState status = OrderState.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
