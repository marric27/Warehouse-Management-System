package com.relatech.warehouse_management_system.stockUnit.dto;

import com.relatech.warehouse_management_system.product.entity.Product;
import com.relatech.warehouse_management_system.util.Category;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUnitDTO {

    private Long id;
    private String batchNumber;
    private LocalDate expirationDate;
    private String productCode;
    private String uniqueCode;
    private Integer quantity;
    private Category category;
    private Product product;
}
