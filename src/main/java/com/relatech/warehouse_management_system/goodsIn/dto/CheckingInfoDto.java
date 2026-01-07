package com.relatech.warehouse_management_system.goodsIn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.common.util.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing the checking info for a stock unit")
public class CheckingInfoDto {

    @Schema(description = "ID of the checking info", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique ID of the Checking info", example = "CI-1", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @NotBlank(message = "Batch number cannot be blank")
    @Schema(description = "Batch number or lot identifier", example = "BN-2025A")
    private String batchNumber;

    @NotNull(message = "Expiration date is required")
    @Schema(description = "Expiration date of the batch", example = "2025-12-31")
    private LocalDate expirationDate;

    @Schema(description = "Quantity of products in this batch", example = "150")
    private Integer quantity;

    @Schema(description = "State of the checking info", example = "OPEN", accessMode = Schema.AccessMode.READ_ONLY)
    private State state;

    @Schema(description = "Soft reference to the associated StockUnit ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long stockUnitId;

    @Schema(description = "reference to the associated GRN Item ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long grnItemId;
}
