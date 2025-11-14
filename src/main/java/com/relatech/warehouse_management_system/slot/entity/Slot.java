package com.relatech.warehouse_management_system.slot.entity;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.util.ProductCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "slot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ProductCategory allowedCategory;

    @Column(nullable = false)
    Integer capacity = 0;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    // associazione logistica:
    // dice che tale prodotto potrebbe essere inserito in quello slot,
    // in seguito questa compatibilità verrà verificata per fare la associazione fisica,
    // ossia mettere fisicamente il prodotto/stock unit nello slot
    private Product prod; // if null then slot is empty

    public boolean canContain(Product p) {
        if (p == null) return false;
        return this.allowedCategory == p.getProductCategory();
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
}
