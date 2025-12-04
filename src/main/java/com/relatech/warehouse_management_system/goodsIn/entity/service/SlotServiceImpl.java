package com.relatech.warehouse_management_system.goodsIn.entity.service;

import com.relatech.warehouse_management_system.common.exception.ResourceNotFoundException;
import com.relatech.warehouse_management_system.goodsIn.dto.StockUnitDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.StockUnitMapper;
import com.relatech.warehouse_management_system.goodsIn.exception.UpdateEntityException;
import com.relatech.warehouse_management_system.product.repository.ProductRepository;
import com.relatech.warehouse_management_system.goodsIn.dto.SlotDTO;
import com.relatech.warehouse_management_system.goodsIn.entity.Slot;
import com.relatech.warehouse_management_system.goodsIn.entity.mapper.SlotMapper;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.SlotRepository;
import com.relatech.warehouse_management_system.goodsIn.entity.StockUnit;
import com.relatech.warehouse_management_system.goodsIn.entity.repository.StockUnitRepository;
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
    
    @Autowired
    private SlotMapper slotMapper;

    @Autowired
    private StockUnitMapper stockUnitMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAllSlots() {
        return slotRepository.findAll()
                .stream()
                .map(slotMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SlotDTO getSlotById(Long id) throws ResourceNotFoundException {
        return slotRepository.findById(id)
                .map(slotMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));
    }

    @Override
    @Transactional
    public SlotDTO createSlot(SlotDTO slotDTO) {
        Slot slot = slotMapper.toEntity(slotDTO);
        Slot savedSlot = slotRepository.save(slot);
        return slotMapper.toDto(savedSlot);
    }

    @Override
    @Transactional
    public SlotDTO updateSlot(Long id, SlotDTO slotDTO) throws ResourceNotFoundException, UpdateEntityException {
        Slot existingSlot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        existingSlot.setCode(slotDTO.getCode());
        existingSlot.setCapacity(slotDTO.getCapacity());

        if (existingSlot.getProd() == null)
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        else if (existingSlot.getProd().getCategory().equals(slotDTO.getAllowedCategory())) {
            existingSlot.setAllowedCategory(slotDTO.getAllowedCategory());
        } else throw new UpdateEntityException(id);
        if (slotDTO.getStockUnits() != null) {
            for (StockUnitDTO stockUnitDTO : slotDTO.getStockUnits()) {
                if (stockUnitDTO.getId() != null) {
                    // Entità già persistente: fetch dal DB
                    StockUnit stockUnit = stockUnitRepository.findById(stockUnitDTO.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("StockUnit", stockUnitDTO.getId()));
                    stockUnit.setSlot(existingSlot); // Associo allo slot
                    existingSlot.getStockUnits().add(stockUnit);
                } else {
                    // Nuova entità: creo e associo
                    StockUnit stockUnit = stockUnitMapper.toEntity(stockUnitDTO);
                    stockUnit.setSlot(existingSlot);
                    existingSlot.getStockUnits().add(stockUnit);
                }
            }
        }
        Slot updatedSlot = slotRepository.save(existingSlot);
        return slotMapper.toDto(updatedSlot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long id) throws ResourceNotFoundException {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slot", id));

        if (slot.getProd() != null) {
            throw new IllegalStateException("Cannot delete slot because it contains a product");
        }
        if (slot.getStockUnits() != null && !slot.getStockUnits().isEmpty()) {
            throw new IllegalStateException("Cannot delete slot because it contains stock units");
        }

        slotRepository.deleteById(id);
    }

    //TODO remove
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

        return slotMapper.toDto(slotRepository.save(slot));
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

        return slotMapper.toDto(savedSlot);
    }


}
