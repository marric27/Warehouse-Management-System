package com.relatech.warehouse_management_system.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDto;
import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.common.util.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object representing a storage slot")
public class SlotDto {
    @Schema (accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Unique code of the slot", example = "SLOT-001", accessMode = Schema.AccessMode.READ_ONLY)
    private String code;

    @Schema(description = "Category allowed in this slot", example = "CONTROLLED_DRUG")
    @NotNull(message = "The allowed category cannot be null.")
    private Category allowedCategory;

    @Schema(description = "Maximum capacity of items in the slot", example = "100")
    @NotNull(message = "The capacity cannot be null.")
    private int capacity;

    @Schema(description = "Product assigned to this slot (optional)")
    private ProductDTO product;

    @Schema(description = "List of stock units stored in this slot", accessMode = Schema.AccessMode.READ_ONLY)
    private List<StockUnitDto> stockUnits;
}
