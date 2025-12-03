package com.relatech.warehouse_management_system.goodsIn.entity.mapper;

import com.relatech.warehouse_management_system.product.mapper.ProductMapper;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.Slot;
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
        dto.setProduct(ProductMapper.toDto(slot.getProd()));
        dto.setStockUnits(slot.getStockUnits() != null ?
                new ArrayList<>(stockUnitMapper.toDTO(slot.getStockUnits())) :
                new ArrayList<>());

        return dto;
    }

    public Slot toEntity(SlotDTO dto) {
        if (dto == null) return null;

        Slot slot = new Slot();
        slot.setId(dto.getId());
        slot.setCode(dto.getCode());
        slot.setAllowedCategory(dto.getAllowedCategory());
        slot.setCapacity(dto.getCapacity());
        slot.setProd(ProductMapper.toEntity(dto.getProduct()));
        slot.setStockUnits(dto.getStockUnits() != null ?
                new ArrayList<>(stockUnitMapper.toEntity(dto.getStockUnits())) :
                new ArrayList<>());

        return slot;
    }

}
