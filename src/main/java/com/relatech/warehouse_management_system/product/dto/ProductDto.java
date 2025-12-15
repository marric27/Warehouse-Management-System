package com.relatech.warehouse_management_system.product.dto;

import com.relatech.warehouse_management_system.common.util.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object representing a product")
public class ProductDto {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Unique product code", example = "PRD-001")
    @NotBlank(message = "The code cannot be empty or null.")
    private String code;

    @Schema(description = "Name of the product", example = "Aspirin 500mg")
    @NotBlank(message = "The name cannot be empty or null.")
    private String name;

    @Schema(description = "Category classification of the product", example = "CONTROLLED_DRUG")
    @NotNull(message = "The product category cannot be null.")
    private Category category;
}
