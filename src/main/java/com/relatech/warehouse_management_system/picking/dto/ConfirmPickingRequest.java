package com.relatech.warehouse_management_system.picking.dto;

import com.relatech.warehouse_management_system.common.util.ErrorReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class ConfirmPickingRequest {

    @Schema(example = "PKL-01KCKXFNW3")
    @NotBlank
    private String pickListCode;

    @Schema(example = "PKLI-22")
    @NotBlank
    private String pickListItemCode;

    @Schema(
            description = "Mappa stockUnitCode → quantità pickata",
            example = "{ \"STK-01KCH3N988\": 3, \"STK-01KCH3MHZZ\": 2 }"
    )
    @NotNull(message = "No Qty picked")
    @Size(min = 1, message = "No Qty picked")
    private Map<String, @Positive Integer> stockUnitQuantities;

    private ErrorReason errorReason;

    @Schema(example = "USER-22")
    @NotBlank
    private String user;
}