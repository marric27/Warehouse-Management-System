package com.relatech.warehouse_management_system.warehouse.entity;

import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class SlotMapper {
    private final StockUnitMapper stockUnitMapper;

    public SlotDTO toDto(Slot slot) {
        if (slot == null) return null;

        SlotDTO dto = new SlotDTO();
        dto.setId(slot.getId());
        dto.setCode(slot.getCode());
        dto.setAllowedCategory(slot.getAllowedCategory());
        dto.setCapacity(slot.getCapacity());
        dto.setProduct(slot.getProd() != null ? ProductMapper.toDto(slot.getProd()) : null);
        dto.setStockUnits(stockUnitMapper.toDTO(slot.getStockUnits() != null ? slot.getStockUnits() : new ArrayList<>()));

        return dto;
    }

    public Slot toEntity(SlotDTO dto) {
        if (dto == null) return null;

        Slot slot = new Slot();
        slot.setId(dto.getId());
        slot.setCode(dto.getCode());
        slot.setAllowedCategory(dto.getAllowedCategory());
        slot.setCapacity(dto.getCapacity());
        slot.setProd(dto.getProduct() != null ? ProductMapper.toEntity(dto.getProduct()) : null);
        slot.setStockUnits(stockUnitMapper.toEntity(dto.getStockUnits() != null ? dto.getStockUnits() : new ArrayList<>()));

        return slot;
    }

}
