package com.relatech.warehouse_management_system.common.util;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reason for a picking discrepancy or issue.")
public enum ErrorReason {

    @Schema(description = "Picked quantity is lower than the requested quantity.")
    MISSING_QTY,

    @Schema(description = "Goods were found damaged during picking.")
    DAMAGED_GOODS,

    @Schema(description = "Wrong item or SKU was found in the picking location.")
    WRONG_ITEM,

    @Schema(description = "Goods were expired or not suitable for shipment.")
    EXPIRED_GOODS,

    @Schema(description = "Picking operation was interrupted or cancelled.")
    PICKING_ABORTED,

    @Schema(description = "Other unspecified reason.")
    OTHER
}
