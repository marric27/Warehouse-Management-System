package com.relatech.warehouse_management_system.product.entity;

import com.relatech.warehouse_management_system.common.util.Category;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String code;
    private String name;
    private Category category;
}
