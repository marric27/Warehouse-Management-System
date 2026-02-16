package com.relatech.warehouse_management_system.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.relatech.warehouse_management_system.common.util.Category;


@Entity
@Table(name = "receiving_product_mirror")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductMirror {

    @Id
    private String code;
    private String name;
    private LocalDateTime lastUpdated;
    private Category category;
}