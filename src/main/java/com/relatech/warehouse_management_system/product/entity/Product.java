package com.relatech.warehouse_management_system.product.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import com.relatech.warehouse_management_system.common.util.Category;


@Entity
@Table(name = "receiving_product_mirror")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    private String code;
    private String name;
    private LocalDateTime lastUpdated;
    private Category category;
}