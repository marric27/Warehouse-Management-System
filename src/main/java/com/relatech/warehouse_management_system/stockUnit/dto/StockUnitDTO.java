package com.relatech.warehouse_management_system.stockUnit.dto;

import com.relatech.warehouse_management_system.util.ProductCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

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
    private ProductCategory productCategory;
    private Long slotId;
}
