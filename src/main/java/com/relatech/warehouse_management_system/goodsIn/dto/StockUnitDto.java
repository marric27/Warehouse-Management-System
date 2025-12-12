package com.relatech.warehouse_management_system.goodsIn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.common.util.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object representing a stock unit")
public class StockUnitDto {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Batch number or lot identifier", example = "BN-2025A")
    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @Schema(description = "Expiration date, must be today or in the future", example = "2025-12-31")
    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;

    @Schema(description = "Product code related to this stock unit", example = "PRD-00123")
    @NotBlank(message = "Product code is required")
    private String productCode;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique code identifying this stock unit", example = "STK-00001", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @Schema(description = "Available quantity of items", example = "150")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;

    @Schema(description = "Category classification of the product")
    @NotNull(message = "Category is required")
    private Category category;

    @Schema(description = "Associated product details", accessMode = Schema.AccessMode.READ_ONLY)
    private ProductDTO productDto;

    @Schema(description = "Storage slot information", accessMode = Schema.AccessMode.READ_ONLY)
    private Long slotId;

}
