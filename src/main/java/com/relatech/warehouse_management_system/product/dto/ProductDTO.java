package com.relatech.warehouse_management_system.product.dto;

import com.relatech.warehouse_management_system.util.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @NotBlank(message = "The code cannot be empty or null.")
    String code;
    @NotBlank(message = "The name cannot be empty or null.")
    String name;
    @NotNull(message = "The product category cannot be null.")
    Category category;
    String nationalCode;
}
