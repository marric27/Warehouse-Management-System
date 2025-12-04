package com.relatech.warehouse_management_system.goodsIn.dto;

import com.relatech.warehouse_management_system.common.util.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for GRN item")
public class GrnItemDto {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "The code cannot be empty or null.")
    @Schema(description = "Unique ID of the GRN item", example = "Item-1")
    private String code;

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

    @Schema(description = "State of the GRN item", example = "OPEN")
    private State state;

    @Schema(description = "GRN", accessMode = Schema.AccessMode.READ_ONLY)
    private Long grnId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<CheckingInfoDto> checkingInfoList;
}

