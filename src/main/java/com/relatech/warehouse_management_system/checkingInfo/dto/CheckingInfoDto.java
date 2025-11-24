package com.relatech.warehouse_management_system.checkingInfo.dto;

import com.relatech.warehouse_management_system.util.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing the checking info for a stock unit")
public class CheckingInfoDto {

    @Schema(description = "ID of the checking info", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "The code cannot be empty or null.")
    @Schema(description = "Unique ID of the Checking info", example = "CI-1")
    private String code;

    @NotBlank(message = "Batch number cannot be blank")
    @Schema(description = "Batch number or lot identifier", example = "BN-2025A")
    private String batchNumber;

    @NotNull(message = "Expiration date is required")
    @Schema(description = "Expiration date of the batch", example = "2025-12-31")
    private LocalDate expirationDate;

    @NotNull(message = "Quantity is required")
    @Schema(description = "Quantity of products in this batch", example = "150")
    private Integer quantity;

    @NotNull(message = "State is required")
    @Schema(description = "State of the checking info", example = "OPEN")
    private State state;

    @NotNull(message = "StockUnitId cannot be null")
    @Schema(description = "Soft reference to the associated StockUnit ID", example = "5")
    private Long stockUnitId;
}
