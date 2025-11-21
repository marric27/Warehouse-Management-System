package com.relatech.warehouse_management_system.grnItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for GRN item")
public class GrnItemDto {

    @Schema(description = "Unique ID of the GRN item", example = "1")
    private Long id;

    @NotBlank(message = "Product code cannot be blank")
    @Schema(description = "Product code", example = "PROD-001")
    private String productCode;

    @Min(value = 0, message = "Expected quantity cannot be negative")
    @Schema(description = "Expected quantity to receive", example = "100")
    private int expectedQty;

    @Min(value = 0, message = "Received quantity cannot be negative")
    @Schema(description = "Quantity physically received", example = "95")
    private int receivedQty;

    @Min(value = 0, message = "Compliant quantity cannot be negative")
    @Schema(description = "Quantity passing quality checks", example = "90")
    private int compliantQty;

    @Min(value = 0, message = "Non-compliant quantity cannot be negative")
    @Schema(description = "Quantity failing quality checks", example = "5")
    private int notCompliantQty;

    @Schema(description = "Status of the GRN item", example = "OPEN")
    private String status;
}

