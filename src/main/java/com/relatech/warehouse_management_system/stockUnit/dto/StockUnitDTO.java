package com.relatech.warehouse_management_system.stockUnit.dto;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.util.Category;
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
public class StockUnitDTO {

    @Schema(description = "Database-generated unique identifier", example = "1")
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

    @Schema(description = "Unique code identifying this stock unit", example = "STK-00001")
    @NotBlank(message = "Unique code is required")
    private String uniqueCode;

    @Schema(description = "Available quantity of items", example = "150")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;

    @Schema(description = "Category classification of the product")
    @NotNull(message = "Category is required")
    private Category category;

    @Schema(description = "Associated product details")
    private ProductDTO productDto;

    @Schema(description = "Storage slot information")
    private SlotDTO slotDto;

}
