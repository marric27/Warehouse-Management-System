package com.relatech.warehouse_management_system.grnItem.dto;

import com.relatech.warehouse_management_system.checkingInfo.entity.CheckingInfo;
import com.relatech.warehouse_management_system.util.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrnItemDto {
    private Long id;
    @NotBlank(message = "The product code cannot be empty or null.")
    private String productCode;
    @NotNull(message = "The expected quantity cannot be null.")
    private int expectedQty;
    @NotNull(message = "The received quantity cannot be null.")
    private int receivedQty;
    @NotNull(message = "The compliant quantity cannot be null.")
    private int compliantQty;
    @NotNull(message = "The not compliant quantity cannot be null.")
    private int notCompliantQty;
    @NotNull(message = "The state cannot be null.")
    private State state;

    private List<CheckingInfo> checkingInfoList;
}
