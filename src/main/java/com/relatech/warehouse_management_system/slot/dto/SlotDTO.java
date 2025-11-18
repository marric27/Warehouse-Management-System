package com.relatech.warehouse_management_system.slot.dto;

import com.relatech.warehouse_management_system.product.dto.ProductDTO;
import com.relatech.warehouse_management_system.stockUnit.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.util.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlotDTO {
    private Long id;
    @NotBlank(message = "The code cannot be empty or null.")
    private String code;
    @NotNull(message = "The allowed category cannot be null.")
    private Category allowedCategory;
    @NotNull(message = "The capacity cannot be null.")
    private int capacity;
    private ProductDTO product;
    private List<StockUnitDTO> stockunits;
}
