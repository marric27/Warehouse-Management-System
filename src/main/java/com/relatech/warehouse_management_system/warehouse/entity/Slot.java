package com.relatech.warehouse_management_system.warehouse.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.common.util.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "slot")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String code;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            String ulid = UlidCreator.getUlid().toString();
            this.code = "SLOT-" + ulid.substring(0, 10).toUpperCase();
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Category allowedCategory;

    @Builder.Default
    @Column(nullable = false)
    private Integer capacity = 0;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Product prod;

    @OneToMany(mappedBy = "slot")
    private List<StockUnit> stockUnits;

    public boolean canContain(Product p) {
        if (p == null) return false;
        return this.allowedCategory == p.getCategory();
    }

    public void addProduct(Product p) {
        if (!canContain(p)) {
            throw new IllegalArgumentException("Product category not allowed in this slot");
        }
        if (prod != null && !prod.getId().equals(p.getId())) {
            throw new IllegalArgumentException("This slot already contains another product type");
        }
        this.prod = p;
    }

    public void addStockUnit(StockUnit stockUnit) {
        if (stockUnit == null) {
            throw new IllegalArgumentException("StockUnit cannot be null");
        }
        if (stockUnit.getSlot() != null && stockUnit.getSlot() != this)
            throw new IllegalArgumentException("StockUnit already assigned to another Slot");

        if (this.stockUnits == null) {
            this.stockUnits = new ArrayList<>();
        }
        if (stockUnit.getCategory() != this.allowedCategory) {
            throw new IllegalArgumentException("Category not allowed in this slot");
        }
        this.stockUnits.add(stockUnit);
        stockUnit.setSlot(this);
    }

}
