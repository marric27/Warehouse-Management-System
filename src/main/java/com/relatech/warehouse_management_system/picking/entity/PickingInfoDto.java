package com.relatech.warehouse_management_system.picking.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a pickinginfo containing user, date and stockunit details.")
public class PickingInfoDto {
    @Schema(description = "Unique identifier of the PickingInfo.", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Timestamp of picking.", example = "2025-01-15")
    private LocalDateTime timestamp;

    @Schema(description = "User who picked from stockunit.", example = "USR-01FZ3M7Y8C")
    private String user;

    @Schema(description = "Stock unit from which is picked.", example = "STK-01KCH3MHZZ")
    private String stockUnitCode;

    @Schema(description = "Batch number or lot identifier", example = "BN-2025A")
    @NotBlank(message = "Batch number is required")
    private String batchNumber;

    @Schema(description = "Expiration date, must be today or in the future", example = "2025-12-31")
    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private LocalDate expirationDate;

    @Schema(description = "Available quantity of items", example = "150")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;
}
