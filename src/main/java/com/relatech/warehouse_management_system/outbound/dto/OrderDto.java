package com.relatech.warehouse_management_system.outbound.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.relatech.warehouse_management_system.common.util.OrderState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing an outbound sales order, including its header details and related order lines.")
public class OrderDto {
    @Schema(description = "Database identifier of the order", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "Automatically generated unique code for the order",
            example = "ORD-01FZ3M7Y8C",
            accessMode = Schema.AccessMode.READ_ONLY
    )    private String code;

    @Schema(description = "Date when the order was created or registered", example = "2025-01-15")
    private LocalDate date;

    @Schema(description = "Soft reference to the customer associated with the order", example = "CUST-00123", accessMode = Schema.AccessMode.READ_ONLY)
    private String customerCode;

    @Schema(description = "Order status", example = "OPEN")
    private OrderState state;

    @Schema(description = "List of sales order lines associated with this order", example = """
            [
              {
                "id": 1,
                "productCode": "PRD-001",
                "quantity": 10,
                "status": "OPEN"
              },
              {
                "id": 2,
                "productCode": "PRD-002",
                "quantity": 5,
                "status": "OPEN"
              },
              {
                "id": 3,
                "productCode": "PRD-003",
                "quantity": 20,
                "status": "OPEN"
              }
            ]""")
    private List<SalesOrderLineDto> salesOrderLineList;
}