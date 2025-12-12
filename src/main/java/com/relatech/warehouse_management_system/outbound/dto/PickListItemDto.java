package com.relatech.warehouse_management_system.outbound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a single item within a pick list.")
public class PickListItemDto {

    @Schema(description = "Unique identifier of the pick list item.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "SKU or product identifier.")
    private String productCode;

    @Schema(description = "Quantity to be picked.")
    private Integer quantity;

    @Schema(description = "Warehouse slot/location where the product is stored.")
    private String slotCode;

    @Schema(description = "Sales order code associated with this pick list item.")
    private String salesOrderCode;

    @Schema(description = "Line number of the sales order associated with this item.")
    private String salesOrderLineNumber;
}
