package com.relatech.warehouse_management_system.outbound.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.OrderState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String customerCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderState state;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("salesOrderLineNumber ASC")
    @Builder.Default
    private List<SalesOrderLine> salesOrderLineList = new ArrayList<>();

    public void addSalesOrderLine(SalesOrderLine line) {
        int nextLineNumber = salesOrderLineList.size() + 1;
        line.setSalesOrderLineNumber(nextLineNumber);
        line.setOrder(this);
        salesOrderLineList.add(line);
    }

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "ORD-" + ulid.substring(0, 10).toUpperCase();
        }
    }
}
