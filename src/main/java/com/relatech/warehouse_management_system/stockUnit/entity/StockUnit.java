package com.relatech.warehouse_management_system.stockUnit.entity;

import com.relatech.warehouse_management_system.util.ProductCategory;
import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

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
    private ProductCategory productCategory;

     @ManyToOne(optional = false)
     @JoinColumn(name = "product_id")
     private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name="slot_id")
    private Slot slot;

}

