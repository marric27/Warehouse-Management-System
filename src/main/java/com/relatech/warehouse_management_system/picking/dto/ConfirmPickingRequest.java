package com.relatech.warehouse_management_system.picking.dto;

import com.relatech.warehouse_management_system.common.util.ErrorReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
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
    //private Map<String, @Positive Integer> stockUnitQuantities;
    private List<@Valid StockUnitQuantityDto> stockUnitQuantities;

    private ErrorReason errorReason;

    @Schema(example = "USER-22")
    @NotBlank
    private String user;
}


