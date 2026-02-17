package com.relatech.warehouse_management_system.goodsIn.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.warehouse.entity.Slot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "stock_units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "product_code", nullable = false)
    private String productCode;

    @Column(name = "code", nullable = false, unique = true, length = 14)
    private String code;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "STK-" + ulid.substring(0, 10).toUpperCase();
        }
    }

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    public boolean canContain(Product p) {
        if (p == null) return false;
        return this.category == p.getCategory();
    }

}

