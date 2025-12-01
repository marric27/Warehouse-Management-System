package com.relatech.warehouse_management_system.stockUnit.entity;

import com.relatech.warehouse_management_system.common.util.Category;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private String batchNumber; // lotto

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate; // data di scadenza

    @Column(name = "product_code", nullable = false)
    private String productCode; // codice prodotto

    @Column(name = "unique_code", nullable = false, unique = true)
    private String uniqueCode; // codice univoco

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // quantità prodotto

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    public boolean canContain(Product p) {
        if (p == null) return false;
        return this.category == p.getCategory();
    }

    public void addProduct(Product p) {
        if (!canContain(p)) {
            throw new IllegalArgumentException("Product category not allowed in this stock unit");
        }
        if (product != null && !product.getId().equals(p.getId())) {
            throw new IllegalArgumentException("This stock unit already contains another product type");
        }
        this.product = p;
    }
}

