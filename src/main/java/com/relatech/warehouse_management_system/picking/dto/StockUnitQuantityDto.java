package com.relatech.warehouse_management_system.picking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record StockUnitQuantityDto(
    @NotBlank String suId,
    @Positive Integer quantity
) {}