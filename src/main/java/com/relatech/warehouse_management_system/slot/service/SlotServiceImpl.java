package com.relatech.warehouse_management_system.slot.service;

import com.relatech.warehouse_management_system.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.slot.dto.SlotDTO;
import com.relatech.warehouse_management_system.slot.entity.Slot;
import com.relatech.warehouse_management_system.slot.mapper.SlotMapper;
import com.relatech.warehouse_management_system.slot.repository.SlotRepository;
import com.relatech.warehouse_management_system.stockUnit.entity.StockUnit;
import com.relatech.warehouse_management_system.stockUnit.repository.StockUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotServiceImpl implements SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private StockUnitRepository stockUnitRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(SlotMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SlotDTO getSlotById(Long id) throws ResourceNotFoundException {
        return slotRepository.findById(id)
                .map(SlotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
    }

    @Override
    @Transactional
    public SlotDTO createSlot(SlotDTO slotDTO) {
        Slot slot = SlotMapper.toEntity(slotDTO);
        Slot savedSlot = slotRepository.save(slot);
        return SlotMapper.toDto(savedSlot);
    }

    @Override
    @Transactional
    public SlotDTO updateSlot(Long id, SlotDTO slotDTO) throws Exception {
        Slot existingSlot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        existingSlot.setCode(slotDTO.getCode());
        existingSlot.setCapacity(slotDTO.getCapacity());

        if (existingSlot.getProd() == null)
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        else if (existingSlot.getProd().getCategory().equals(slotDTO.getAllowedCategory())) {
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        } else throw new Exception("Cant update slot category cause contains a product");

        Slot updatedSlot = slotRepository.save(existingSlot);
        return SlotMapper.toDto(updatedSlot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        if (slot.getProd() != null) {
            throw new IllegalStateException("Cannot delete slot because it contains a product");
        }
        if (!slot.getStockUnits().isEmpty()) {
            throw new IllegalStateException("Cannot delete slot because it contains stock units");
        }

        slotRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SlotDTO assignStockUnitToSlot(Long slotId, Long stockUnitId) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        StockUnit stockUnit = stockUnitRepository.findById(stockUnitId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Unit", stockUnitId));

        if (!slot.getAllowedCategory().equals(stockUnit.getCategory())) {
            throw new IllegalArgumentException("StockUnit category not allowed in this Slot");
        }

        slot.addStockUnit(stockUnit);

        return SlotMapper.toDto(slotRepository.save(slot));
    }

    @Override
    @Transactional
    public SlotDTO removeStockUnitFromSlot(Long slotId, Long stockUnitId) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", slotId));
        StockUnit toRemove = slot.getStockUnits().stream()
                .filter(su -> su.getId().equals(stockUnitId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitId));

        slot.getStockUnits().remove(toRemove);

        toRemove.setSlot(null);

        Slot savedSlot = slotRepository.save(slot);

        return SlotMapper.toDto(savedSlot);
    }


}
