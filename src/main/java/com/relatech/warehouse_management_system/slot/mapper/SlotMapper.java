package com.relatech.warehouse_management_system.slot.mapper;

import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.stockUnit.mapper.StockUnitMapper;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {
    public static SlotDTO toDto(Slot slot) {
        if (slot == null) return null;

        SlotDTO dto = new SlotDTO();
        dto.setId(slot.getId());
        dto.setCode(slot.getCode());
        dto.setAllowedCategory(slot.getAllowedCategory());
        dto.setCapacity(slot.getCapacity());
        dto.setProduct(ProductMapper.toDto(slot.getProd()));
        dto.setStockUnits(StockUnitMapper.toDTO(slot.getStockUnits()));

        return dto;
    }

    public static Slot toEntity(SlotDTO dto) {
        if (dto == null) return null;

        Slot slot = new Slot();
        slot.setId(dto.getId());
        slot.setCode(dto.getCode());
        slot.setAllowedCategory(dto.getAllowedCategory());
        slot.setCapacity(dto.getCapacity());
        slot.setProd(ProductMapper.toEntity(dto.getProduct()));
        slot.setStockUnits(StockUnitMapper.toEntity(dto.getStockUnits()));

        return slot;
    }


}
