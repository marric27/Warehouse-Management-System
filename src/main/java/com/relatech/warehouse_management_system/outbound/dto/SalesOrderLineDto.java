package com.relatech.warehouse_management_system.outbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.common.util.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing a single sales order line belonging to an Order.")
public class SalesOrderLineDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Database identifier of the sales order line", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Sales order number", example = "SO-001")
    private String salesOrderNumber;//TODO

    @Schema(description = "Soft reference to the product associated with this line", example = "PROD-987")
    private String productCode;

    @Schema(description = "Quantity of product ordered", example = "10")
    private int quantity;

    @Schema(description = "Status of this order line", example = "OPEN")
    private OrderState status;

    @Schema(description = "ID of the parent order", example = "1")
    private Long orderId;
}