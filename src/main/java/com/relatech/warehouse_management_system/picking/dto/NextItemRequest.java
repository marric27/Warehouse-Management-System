package com.relatech.warehouse_management_system.picking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NextItemRequest {
    @NotBlank
    @Schema(description = "List of pick list ids")
    private List<Long> pickListIds;
}
