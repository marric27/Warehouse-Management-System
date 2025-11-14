package com.relatech.warehouse_management_system.slot.dto;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.util.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlotDTO {
    private Long id;
    @NotBlank(message = "The code cannot be empty or null.")
    private String code;
    @NotNull(message = "The product category cannot be null.")
    private ProductCategory allowedCategory;
    @NotNull(message = "The capacity cannot be null.")
    private int capacity;
    private ProductDTO product;
}
